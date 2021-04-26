package com.myapp.travelize.main.mainscreen


import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myapp.travelize.Keys
import com.myapp.travelize.R
import com.myapp.travelize.interfaces.JsonPlaceHolderApi
import com.myapp.travelize.main.MainHostActivity2
import com.myapp.travelize.models.Places
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {
    lateinit var mLocationManager: LocationManager
    lateinit var myLocation: Location


    val base_url = "https://maps.googleapis.com/maps/api/"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    lateinit var jsonPlaceHolderApi: JsonPlaceHolderApi
    val context = activity as? MainHostActivity2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        Log.e("HomeFragment", "created")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userLocation=getUserGeographicCoordinates()
        if (userLocation != null) {
            callAPI(userLocation.latitude,userLocation.longitude)
        }else{
            Log.e("location","is null")
        }
    }

    @SuppressLint("MissingPermission", "LongLogTag")
    private fun getUserGeographicCoordinates(): Location? {
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        Log.e("lm", locationManager.toString())
        val location = getLastKnownLocation()
        Log.e("locn", location.toString())

        var latitude: Double = location?.latitude ?: 0.0
        var longitude: Double = location?.longitude ?: 0.0
        Log.e("user latitude&longitude", "${latitude}&${longitude}")

        val locationListener = LocationListener {
            Log.e("locationListener", "invoked!")
            latitude = it.latitude
            longitude = it.longitude
            Log.e("user latitude&longitude(in)", "${latitude}&${longitude}")
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1000.00f, locationListener)
        return location
    }//getUserGeographicCoordinates ends

    private fun callAPI(latitude: Double, longitude: Double) {
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call = jsonPlaceHolderApi.doPlaces("${latitude},${longitude}", "1500", "restaurant", "food", Keys.apiKey())

        call.enqueue(object : Callback<Places.Root?> {
            override fun onResponse(call: Call<Places.Root?>?, response: Response<Places.Root?>?) {
                if (response != null) {
                    if (!response.isSuccessful()) {
                        Log.e("Response", "failed:(")
                        Log.e("Response", response.toString())
                        return
                    }
                } else {
                    Log.e("Response", "null")
                }
                val body: Places.Root? = response!!.body()
                val apiResults: MutableList<Places.Result>? = body!!.getResults()
                if (apiResults != null) {
                    for (result in apiResults) {
                        Log.e("Name:", result.name)
                        Log.e("Address", result.vicinity)
                        Log.e("Open", result.openingHour?.open.toString())
                        Log.e("Rating", result.rating.toString())
                        Log.e("Photo Ref.", result.photos?.get(0)?.photoReference.toString())
                        Log.e("Place Id", result.placeID)
                    }
                }else{
                    Log.e("apiResponse","is null")
                }
                Log.i("Response", body.toString())
            }
            override fun onFailure(call: Call<Places.Root?>?, t: Throwable?) {
                t!!.printStackTrace()
            }
        })
    }//callAPI ends

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        mLocationManager =
            requireContext().getApplicationContext().getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }
        return bestLocation
    }
}