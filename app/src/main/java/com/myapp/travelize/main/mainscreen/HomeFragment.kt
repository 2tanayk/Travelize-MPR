package com.myapp.travelize.main.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.myapp.travelize.Keys
import com.myapp.travelize.R
import com.myapp.travelize.interfaces.JsonPlaceHolderApi
import com.myapp.travelize.models.Places
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {
    val base_url = "https://maps.googleapis.com/maps/api/"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    lateinit var jsonPlaceHolderApi: JsonPlaceHolderApi
//    val jsonPlaceHolderApi: JsonPlaceHolderApi? = null

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

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call = jsonPlaceHolderApi.doPlaces("-33.8670522,151.1957362", "1500", "restaurant", "cruise", Keys.apiKey())

        call.enqueue(object : Callback<Places.Root?> {
            override fun onResponse(call: Call<Places.Root?>?, response: Response<Places.Root?>?) {
                if (response != null) {
                    if (!response.isSuccessful()) {
                        Log.e("Respnse","failed:(")
                        Log.e("Respnse",response.toString())
                        return
                    }
                }else{
                    Log.e("Response","null")
                }
                val body:Places.Root? = response!!.body()
                val apiResults: MutableList<Places.Result>? = body!!.getResults()

                if (apiResults != null) {
                    for(result in apiResults) {
                        Log.e("Name:",result.name)
                        Log.e("Address",result.vicinity)
                        Log.e("Open", result.openingHour.open.toString())
                        Log.e("Rating", result.rating.toString())
                        Log.e("Photo Ref.",result.photos[0].photoReference)
                        Log.e("Place Id",result.placeID)
                    }
                }

                Log.i("Response", body.toString())


            }

            override fun onFailure(call: Call<Places.Root?>?, t: Throwable?) {
                t!!.printStackTrace()
            }
        })

    }
}