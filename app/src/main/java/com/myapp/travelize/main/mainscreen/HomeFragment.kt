package com.myapp.travelize.main.mainscreen


import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Context.MODE_PRIVATE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.travelize.Keys
import com.myapp.travelize.R
import com.myapp.travelize.adapters.PlaceAdapter
import com.myapp.travelize.authentication.MainActivity
import com.myapp.travelize.authentication.MainActivity.Companion.FIRESTORE_SHARED_PREF
import com.myapp.travelize.interfaces.JsonPlaceHolderApi
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.main.MainHostActivity2
import com.myapp.travelize.main.MainHostActivity2.Companion.KEYWORD_MALL
import com.myapp.travelize.main.MainHostActivity2.Companion.KEYWORD_PARK
import com.myapp.travelize.main.MainHostActivity2.Companion.KEYWORD_RESTAURANT
import com.myapp.travelize.main.MainHostActivity2.Companion.KEYWORD_THEATER
import com.myapp.travelize.main.MainHostActivity2.Companion.TYPE_MALL
import com.myapp.travelize.main.MainHostActivity2.Companion.TYPE_PARK
import com.myapp.travelize.main.MainHostActivity2.Companion.TYPE_RESTAURANT
import com.myapp.travelize.main.MainHostActivity2.Companion.TYPE_THEATER
import com.myapp.travelize.main.MainHostActivity2.Companion.USER_LAT
import com.myapp.travelize.main.MainHostActivity2.Companion.USER_LONG
import com.myapp.travelize.models.Place
import com.myapp.travelize.models.PlaceType
import com.myapp.travelize.models.networking.DetailResponse
import com.myapp.travelize.models.networking.PlaceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment(), PlaceAdapter.OnItemClickListener {

//    companion object {
//        const val TAG = "fragment_tag"
//        const val PLACE_LIST = "place_list"
//        const val DROPDOWN_MENU_ITEM = "dropdown_menu_item"
//        fun newInstance(tag: String, placesList:MutableList<Place>,dropDownMenuItem:Int): Fragment {
//            val fragment = HomeFragment()
//            val bundle = Bundle()
//            bundle.putString(TAG, tag)
//            bundle.putParcelableArrayList(PLACE_LIST,ArrayList<Parcelable>(placesList))
//            bundle.putInt(DROPDOWN_MENU_ITEM, dropDownMenuItem)
//            fragment.arguments = bundle
//            return fragment
//        }
//    }

    lateinit var mLocationManager: LocationManager
    lateinit var jsonPlaceHolderApi: JsonPlaceHolderApi
    lateinit var placesAdapter: PlaceAdapter
    lateinit var placesRecyclerView: RecyclerView
    lateinit var typeAutoCompleteTextView: AutoCompleteTextView
    lateinit var context: MainHostActivity2

    val placesList: MutableList<Place> = mutableListOf()
    val typeList: MutableList<PlaceType> = mutableListOf()
    val base_url = "https://maps.googleapis.com/maps/api/"
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val f:Boolean= arguments?.getBoolean("State Retained",false) ?:false
        Log.e("HomeFragment onCreate", "called!")
        context = (activity as? MainHostActivity2)!!
        context.askFineLocationPermission()
        placesAdapter = PlaceAdapter(this)
        val userLocation = getUserGeographicCoordinates()
//        if (f)
//        {
//            Log.e("is state retained", f.toString())
//            placesAdapter.submitList(placesList)
//            return
//        }
        if (userLocation != null) {
            context.saveUserLocation(userLocation.latitude, userLocation.longitude)
            callPlacesAPI(
                userLocation.latitude,
                userLocation.longitude,
                TYPE_RESTAURANT,
                KEYWORD_RESTAURANT
            )
        } else {
            Log.e("location", "is null")
        }
    }

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        Log.e("HomeFragment onCreateView", "called!")
        Log.e("HomeFragment", "created")
        return view
    }

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("HomeFragment onViewCreated", "called!")
//        placeTypeMenu = view.findViewById(R.id.select_type_exposed_menu)
        placesRecyclerView = view.findViewById(R.id.places_recycler_view)
        typeAutoCompleteTextView = view.findViewById(R.id.type_text_view)
        placesRecyclerView.setHasFixedSize(true)
        placesRecyclerView.adapter = placesAdapter
        //createTypeList()
        createPlaceTypesMenu()
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
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            1000.00f,
            locationListener
        )
        return location
    }//getUserGeographicCoordinates ends

    private fun callPlacesAPI(
        latitude: Double,
        longitude: Double,
        type: String,
        keyword: String,
        radius: String = "1500",
        isBeingUpdated: Boolean = false
    ) {
        Log.e("current thread", Thread.currentThread().name)
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call = jsonPlaceHolderApi.doPlaces(
            "${latitude},${longitude}",
            radius,
            type,
            keyword,
            Keys.apiKey()
        )

        call.enqueue(object : Callback<PlaceResponse.Root?> {
            override fun onResponse(
                call: Call<PlaceResponse.Root?>?,
                response: Response<PlaceResponse.Root?>?
            ) {
                if (response != null) {
                    if (!response.isSuccessful()) {
                        Log.e("Response", "failed:(")
                        Log.e("Response", response.toString())
                        return
                    }
                } else {
                    Log.e("Response", "null")
                    return
                }
                Log.e("current thread", Thread.currentThread().name)
                val body: PlaceResponse.Root? = response.body()
                val apiResults: MutableList<PlaceResponse.Result>? = body!!.getResults()
                if (apiResults != null) {
                    for (result in apiResults) {
                        val id = result.placeID
                        val name = result.name
                        val photoRef = result.photos?.get(0)?.photoReference
                        val address = result.vicinity
                        val rating = result.rating
                        val totalRatings: Int = result.totalRatings ?: 0
                        val place = Place(id, name, photoRef, address, rating, totalRatings)
                        //sub call
                        subcallAPI(id, place)
                        placesList.add(place)

                        Log.e("Name:", result.name)
                        Log.e("Address", result.vicinity)
                        Log.e("Open", result.openingHour?.open.toString())
                        Log.e("Rating", result.rating.toString())
                        Log.e("Photo Ref.", result.photos?.get(0)?.photoReference.toString())
                        Log.e("Place Id", result.placeID)
                    }
                    if (isBeingUpdated) {
                        placesAdapter.notifyDataSetChanged()
                    } else {
                        placesAdapter.submitList(placesList)
                    }
                } else {
                    Log.e("apiResponse", "is null")
                }
                Log.i("Response", body.toString())
            }//onResponse ends

            private fun subcallAPI(id: String, place: Place) {
                val subcall = jsonPlaceHolderApi.getDetail(
                    id,
                    "opening_hours,formatted_phone_number",
                    Keys.apiKey()
                )
                subcall.enqueue(object : Callback<DetailResponse.Root?> {
                    override fun onResponse(
                        call: Call<DetailResponse.Root?>,
                        response: Response<DetailResponse.Root?>
                    ) {
                        if (!response.isSuccessful()) {
                            Log.e("subcall Response", "failed:(")
                            Log.e("subcall Response", response.toString())
                            return
                        }
                        Log.e("current thread", Thread.currentThread().name)
                        val body: DetailResponse.Root? = response.body()
                        val subCallResult = body!!.getResult()
                        if (subCallResult != null) {
                            place.phoneNo = subCallResult.phoneNo
                            place.workingHours =
                                subCallResult.openingHour?.workTimings ?: mutableListOf()
                            Log.e("subCallResult", "success!")
                        } else {
                            Log.e("subCallResult", "null")
                        }
                    }

                    override fun onFailure(call: Call<DetailResponse.Root?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }//subcallAPI ends

            override fun onFailure(call: Call<PlaceResponse.Root?>?, t: Throwable?) {
                t!!.printStackTrace()
            }//onFailure ends
        })
    }//callAPI ends

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        mLocationManager =
            requireContext().applicationContext
                .getSystemService(LOCATION_SERVICE) as LocationManager
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

    override fun onItemClick(position: Int) {
        //Toast.makeText(activity, "Clicked $position", Toast.LENGTH_SHORT).show()
        val place = placesList[position]
        place.isExpanded = !place.isExpanded
        placesAdapter.notifyItemChanged(position)
    }

    private fun createPlaceTypesMenu() {
        val placeTypeList = listOf("Restaurants", "Malls", "Theatres", "Parks")
        val menuAdapter = ArrayAdapter(requireActivity(), R.layout.menu_list_item, placeTypeList)
        typeAutoCompleteTextView.setAdapter(menuAdapter)
        typeAutoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
            Log.e("Menu", "clicked ${i} ${placeTypeList[i]}")
            var type: String? = null
            var keyword: String? = null
            when (i) {
                0 -> {
                    type = TYPE_RESTAURANT
                    keyword = KEYWORD_RESTAURANT
                }
                1 -> {
                    type = TYPE_MALL
                    keyword = KEYWORD_MALL
                }
                2 -> {
                    type = TYPE_THEATER
                    keyword = KEYWORD_THEATER
                }
                3 -> {
                    Log.e("when", "in park")
                    type = TYPE_PARK
                    keyword = KEYWORD_PARK
                }
            }
            placesList.clear()
            placesAdapter.notifyDataSetChanged()
            updatePlacesRecyclerView(type, keyword)
        }
    }

    private fun updatePlacesRecyclerView(type: String?, keyword: String?) {
        val sharedPrefs = activity?.getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val latitude = sharedPrefs?.getString(USER_LAT, null)?.toDouble()
        val longitude = sharedPrefs?.getString(USER_LONG, null)?.toDouble()
        if (latitude != null && longitude != null && type != null && keyword != null) {
            callPlacesAPI(latitude, longitude, type, keyword, isBeingUpdated = true)
        } else {
            Log.e("callAPI", "some parameter is null")
        }
    }

    override fun onPlaceRegister(position: Int) {
        Log.e("append btn", "clicked!")
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Confirmation")
            .setMessage("Do you want company for this destination?")
            .setNegativeButton("No") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Yes") { dialog, which ->
                necessaryDocCheck(placesList[position].id, placesList[position].name, placesList[position].photoRef)
            }.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
    }

    fun fetchUserPassions(id: String?) {
        val sharedPref = context.getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val tempSet: Set<String> = HashSet<String>(sharedPref.getStringSet(MainHostActivity.USER_PASSIONS, HashSet<String>()))
        var name: String? =sharedPref.getString(MainActivity.USER_NAME, null)
        var passionsList: List<String> = tempSet.toList()
        if(passionsList.isEmpty() || name==null)
        {
            db.collection("Users").document(firebaseAuth.currentUser.uid).get().addOnSuccessListener {
                if (it != null) {
                    Log.e("user doc snapshot", "DocumentSnapshot data: ${it.data}")
                    passionsList= it["passions"] as List<String>
                    name=it.getString("name")
                    addUserToChatGroup(passionsList,name,id)
                } else {
                    Log.e("user doc snapshot", "No such document")
                }
            }.addOnFailureListener {
                Log.e("user doc snapshot","error :( ${it.printStackTrace()}")
            }
        }else{
            addUserToChatGroup(passionsList,name,id)
        }
    }

    fun addUserToChatGroup(passionsList: List<String>,name: String?, id: String?) {
        val userSubCollectionRef = db.collection("Places")
            .document("${id}")
            .collection("users")
        val userPlaceRegisterData = hashMapOf(
            "passions" to passionsList,
            "name" to name
        )
        userSubCollectionRef.document(firebaseAuth.currentUser.uid).set(userPlaceRegisterData).addOnSuccessListener {
            Log.e("passionList","added to place users collection")
        }.addOnFailureListener {
            Log.e("passionList","failed :( ${it.printStackTrace()}")
        }
    }

    fun necessaryDocCheck(id: String?, name: String?, photoRef: String?) {
        Log.e("clicked place id", "${id}")
        val placeDocRef = db.collection("Places").document("${id}")
        placeDocRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val doc = it.result
                if (doc != null) {
                    if (doc.exists()) {
                        Log.d("Place doc", "Document exists!")
                        fetchUserPassions(id)
                    } else {
                        Log.d("Place doc", "Document does not exist,creating..")
                        val placeDetails = hashMapOf(
                            "name" to name,
                            "url" to photoRef
                        )
                        placeDocRef.set(placeDetails).addOnSuccessListener {
                            Log.e("Place doc write", "success!")
                            fetchUserPassions(id)
                        }.addOnFailureListener {
                            Log.e("Place doc write", "failure :( ${it.stackTrace}")
                        }
                    }//innermost else
                } else {
                    Log.e("Place doc is", "null")
                }//inner else ends
            }//outer if ends
            else{
            Log.e("Place doc","not successful")
            }//outermost else ends
        }//onComplete callback ends
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("onAttach", "called!")
    }

    //ChIJ2Qc9VhS35zsRIiRt5Dyo4YY ChIJw7Q94Ti35zsRTLvuiPe3p8g
    override fun onStart() {
        super.onStart()
        Log.e("HomeFragment onStart", "called!")
    }

    override fun onResume() {
        super.onResume()
        Log.e("HomeFragment onResume", "called!")
    }

    override fun onPause() {
        super.onPause()
        Log.e("HomeFragment onPause", "called!")
    }

    override fun onStop() {
        super.onStop()
        Log.e("HomeFragment onStop", "called!")
    }

    @SuppressLint("LongLogTag")
    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("HomeFragment onDestroyView", "called!")
        context.saveFragmentState(R.id.item_home)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HomeFragment onDestroy", "called!")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("HomeFragment onDetach", "called!")
    }

//    private fun createTypeList() {
//        typeList.add(PlaceType(R.drawable.ic_restaurant, "Restaurants"))
//        typeList.add(PlaceType(R.drawable.ic_mall, "Malls"))
//        typeList.add(PlaceType(R.drawable.ic_movie, "Theatres"))
//        typeList.add(PlaceType(R.drawable.ic_park, "Parks"))
//    }
}