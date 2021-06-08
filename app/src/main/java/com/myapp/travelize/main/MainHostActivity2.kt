package com.myapp.travelize.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.travelize.Constants.Companion.ACTION_CHAT_GROUP_SELECTED
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PASSIONS
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP_CAMERA
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP_DELETE
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP_GALLERY
import com.myapp.travelize.Constants.Companion.ACTION_KEY
import com.myapp.travelize.R
import com.myapp.travelize.authentication.MainActivity.Companion.FIRESTORE_SHARED_PREF
import com.myapp.travelize.authentication.MainActivity.Companion.USER_EMAIL
import com.myapp.travelize.authentication.MainActivity.Companion.USER_NAME
import com.myapp.travelize.interfaces.FragmentActionListener
import com.myapp.travelize.main.MainHostActivity.Companion.PROFILE_PIC_URL
import com.myapp.travelize.main.MainHostActivity.Companion.USER_DOB
import com.myapp.travelize.main.MainHostActivity.Companion.USER_GENDER
import com.myapp.travelize.main.MainHostActivity.Companion.USER_INSTITUTE_NAME
import com.myapp.travelize.main.MainHostActivity.Companion.USER_PASSIONS
import com.myapp.travelize.main.createprofile.CreateProfileFragment2
import com.myapp.travelize.main.mainscreen.ChatFragment
import com.myapp.travelize.main.mainscreen.ChatHostFragment
import com.myapp.travelize.main.mainscreen.HomeFragment
import com.myapp.travelize.main.mainscreen.ProfileFragment
import com.myapp.travelize.models.User


class MainHostActivity2 : AppCompatActivity(),FragmentActionListener {
    companion object {
        const val USER_LAT = "latitude"
        const val USER_LONG = "longitude"
        const val TYPE_RESTAURANT = "restaurant"
        const val KEYWORD_RESTAURANT = "food"
        const val TYPE_MALL = "shopping_mall"
        const val KEYWORD_MALL = "shop"
        const val TYPE_THEATER = "movie_theater"
        const val KEYWORD_THEATER = "movie"
        const val TYPE_PARK = "park"
        const val KEYWORD_PARK = "park"
        const val HOME_FRAGMENT_TAG = "home"
        const val CHAT_HOST_FRAGMENT_TAG = "chat_host"
        const val PROFILE_FRAGMENT_TAG = "profile"
        const val CHAT_FRAGMENT_TAG = "chat"
        const val PASSIONS_FRAGMENT_TAG = "passions"
        const val CHAT_GROUP_KEY="chat_group_key"
        const val CHAT_DOC_REF="chat_document_reference"
        const val CHAT_BACKSTACK="chat_backstack"
        const val SAVED_STATE_CONTAINER_KEY = "ContainerKey"
        const val SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.e("Permission", "Granted")
            } else {
                Log.e("Permission", "Denied")
            }
        }

    private val requestAccessFineLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.e("Location Permission", "Granted")
            } else {
                Log.e("Location Permission", "Denied")
            }
        }
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("Users")
    private var savedStateSparseArray = SparseArray<Fragment.SavedState>()
    private var currentSelectItemId = R.id.item_home
    lateinit var fragmentManager: FragmentManager
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var pfpBottomSheetDialogFragment: PfpBottomSheetDialogFragment
    val docRef = collectionRef.document(firebaseAuth.getCurrentUser().getUid())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host2)
        supportActionBar?.hide()
        fragmentManager = supportFragmentManager
        pfpBottomSheetDialogFragment= PfpBottomSheetDialogFragment()
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val i = intent
        val isNewUser = i.getBooleanExtra("New User", false)
        Log.e("Info", "MainHostActivity2 and New User Status:$isNewUser")
        if (isNewUser) {
            createUserProfile()
        }
        askFineLocationPermission()

        bottomNavigationView.setOnNavigationItemSelectedListener {
//            val homeFragment = fragmentManager.findFragmentByTag(HOME_FRAGMENT_TAG) as? HomeFragment
//            Log.e("homeFragment check", homeFragment.toString())
//            val chatFragment= fragmentManager.findFragmentByTag(CHAT_HOST_FRAGMENT_TAG) as? ChatHostFragment
//            Log.e("chatFragment check", chatFragment.toString())
//            val profileFragment = fragmentManager.findFragmentByTag(PROFILE_FRAGMENT_TAG) as? ProfileFragment
//            Log.e("profileFragment check", profileFragment.toString())
            when (it.itemId) {
                R.id.item_home -> {
                    Log.e("check menu items", it.toString())
                    Log.e("HomeItem", "clicked")
                    if(fragmentManager.findFragmentById(R.id.main_fragment_container2) !is HomeFragment)
                    {
                        saveFragmentState(it.itemId)
                        val homeFragment=HomeFragment()
                        val savedState=savedStateSparseArray.get(it.itemId,null)
                        if(savedState!=null) {
                            Log.e("SavedState","is not null")
                            homeFragment.setInitialSavedState(savedState)
                            val tempBundle=Bundle()
                            tempBundle.putBoolean("State Retained",true)
                            homeFragment.arguments=tempBundle
                        }
                            fragmentManager.beginTransaction()
                                .replace(R.id.main_fragment_container2,homeFragment, HOME_FRAGMENT_TAG)
                                .commit()
                    }else{
                        Log.e("HomeFragment","already in the container")
                    }
                }

                R.id.item_chat_host -> {
                    Log.e("ChatHostItem", "clicked")
                    if(fragmentManager.findFragmentById(R.id.main_fragment_container2) !is ChatHostFragment)
                    {
                        saveFragmentState(it.itemId)
                        val chatHostFragment=ChatHostFragment()
                        chatHostFragment.fragmentActionListener=this
                        val savedState=savedStateSparseArray.get(it.itemId,null)
                        if(savedState!=null) {
                            Log.e("SavedState","is not null")
                            chatHostFragment.setInitialSavedState(savedState)
                            val tempBundle=Bundle()
                            tempBundle.putBoolean("State Retained",true)
                            chatHostFragment.arguments=tempBundle
                        }
                        fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container2,chatHostFragment, CHAT_HOST_FRAGMENT_TAG)
                            .commit()
                    }else{
                        Log.e("ChatHostFragment","already in the container")
                    }
                }

                R.id.item_profile -> {
                    Log.e("ProfileItem", "clicked")
                    if(fragmentManager.findFragmentById(R.id.main_fragment_container2) !is ProfileFragment)
                    {
                        saveFragmentState(it.itemId)
                        val profileFragment=ProfileFragment()
                        profileFragment.fragmentActionListener=this
                        val savedState=savedStateSparseArray.get(it.itemId,null)
                        if(savedState!=null) {
                            Log.e("SavedState","is not null")
                            profileFragment.setInitialSavedState(savedState)
                            val tempBundle=Bundle()
                            tempBundle.putBoolean("State Retained",true)
                            profileFragment.arguments=tempBundle
                        }
                        fragmentManager
                            .beginTransaction()
                            .replace(R.id.main_fragment_container2,profileFragment, PROFILE_FRAGMENT_TAG)
                            .commit()
                    }else{
                        Log.e("ProfileFragment","already in the container")
                    }
                }
                else->{
                    Log.e("InvalidItem","clicked:(")
                }
            }
            true
        }
    }

    fun askFineLocationPermission() {
        if (!hasAccessFineLocationPermission()) {
            requestAccessFineLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun saveFragmentState(actionId:Int) {
        val currentFragment = fragmentManager.findFragmentById(R.id.main_fragment_container2)
        if (currentFragment != null) {
            savedStateSparseArray.put(currentSelectItemId, fragmentManager.saveFragmentInstanceState(currentFragment))
            Log.e("State saved for","${currentSelectItemId}")
        }
        currentSelectItemId=actionId
    }

    fun restoreFragmentState(actionId: Int)
    {

    }

    private fun createUserProfile() {
        val sharedPref = getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val name = sharedPref.getString(USER_NAME, null)
        val email = sharedPref.getString(USER_EMAIL, null)
        val dob = sharedPref.getString(USER_DOB, null)
        val gender = sharedPref.getString(USER_GENDER, null)
        val tempSet: Set<String> =
            HashSet<String>(sharedPref.getStringSet(USER_PASSIONS, HashSet<String>()))
        val passions: List<String> = tempSet.toList()
        val imageURL = sharedPref.getString(PROFILE_PIC_URL, null)
        val institute = sharedPref.getString(USER_INSTITUTE_NAME, null)

        val user = User(
            name,
            email,
            dob,
            gender,
            institute,
            passions,
            imageURL,
            null,
            null,
            null,
            null,
            null
        )

        docRef
            .set(user).addOnSuccessListener {
                Log.e("MainHostActivity2", "DocumentSnapshot successfully written!");
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.e("MainHostActivity2", "Failure to write document")
            }
    }

    fun hasAccessFineLocationPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun saveUserLocation(latitude: Double, longitude: Double) {
        val sharedPref = getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(USER_LAT, latitude.toString())
        editor.putString(USER_LONG, longitude.toString())
        editor.apply()
        Log.e("All prefs", sharedPref.all.toString())
    }

    private fun addChatFragment(bundle: Bundle) {
        val chatFragment=ChatFragment()
        chatFragment.arguments=bundle
        bottomNavigationView.visibility=GONE
        fragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container2,chatFragment, CHAT_FRAGMENT_TAG)
            .addToBackStack(CHAT_BACKSTACK)
            .commit()
    }

    private fun addEditPassionsFragment(bundle: Bundle) {
        val editPassionsFragment=CreateProfileFragment2()
        editPassionsFragment.arguments=bundle
        bottomNavigationView.visibility= GONE
        fragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment_container2,editPassionsFragment, PASSIONS_FRAGMENT_TAG)
            .addToBackStack(CHAT_BACKSTACK)
            .commit()
    }

    private fun showPfpBottomSheetDialogFragment(bundle: Bundle) {
        pfpBottomSheetDialogFragment.fragmentActionListener=this
        pfpBottomSheetDialogFragment.show(fragmentManager,PfpBottomSheetDialogFragment.TAG)
    }

    private fun callDeleteUserPfp() {
        val currentFragment=fragmentManager.findFragmentById(R.id.main_fragment_container2)
        if( currentFragment is ProfileFragment)
        {
            currentFragment.deleteUserPfp()
        }
    }

    private fun callReplaceUserPfpGallery() {
        if (!hasExternalStoragePermission()) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val currentFragment=fragmentManager.findFragmentById(R.id.main_fragment_container2)
        if( currentFragment is ProfileFragment)
        {
            if (hasExternalStoragePermission()) {
                currentFragment.getImageFromGalleryForPfp()
            }
        }
    }

    private fun callReplaceUserPfpCamera() {
        if (!hasCameraPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        val currentFragment=fragmentManager.findFragmentById(R.id.main_fragment_container2)
        if( currentFragment is ProfileFragment)
        {
            if(hasCameraPermission()) {
                currentFragment.captureImageForPfp()
            }
        }
    }

    override fun onActionCallBack(bundle: Bundle) {
        Log.e("onActionCallBack","called!")
        val actionPerformed = bundle.getInt(ACTION_KEY)

        when (actionPerformed) {
            ACTION_CHAT_GROUP_SELECTED -> {
                addChatFragment(bundle)
            }
            ACTION_EDIT_PASSIONS -> {
                addEditPassionsFragment(bundle)
            }
            ACTION_EDIT_PFP -> {
                showPfpBottomSheetDialogFragment(bundle)
            }
            ACTION_EDIT_PFP_DELETE -> {
                callDeleteUserPfp()
            }
            ACTION_EDIT_PFP_GALLERY -> {
                callReplaceUserPfpGallery()
            }
            ACTION_EDIT_PFP_CAMERA -> {
                callReplaceUserPfpCamera()
            }
        }
    }

    fun hasExternalStoragePermission() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun onBackPressed() {
        if(this::fragmentManager.isInitialized && fragmentManager.findFragmentById(R.id.main_fragment_container2) is ChatFragment)
        {
            Log.e("onBack","special condition called!")
            bottomNavigationView.visibility= VISIBLE
        }
        super.onBackPressed()
    }
}
