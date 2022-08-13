package com.myapp.travelize.main.mainscreen

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PASSIONS
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP
import com.myapp.travelize.Constants.Companion.ACTION_KEY
import com.myapp.travelize.R
import com.myapp.travelize.authentication.MainActivity.Companion.FIRESTORE_SHARED_PREF
import com.myapp.travelize.interfaces.FragmentActionListener
import com.myapp.travelize.main.MainHostActivity.Companion.PROFILE_PIC_URL
import com.myapp.travelize.main.MainHostActivity.Companion.USER_PASSIONS
import com.myapp.travelize.main.MainHostActivity2
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val collectionRef = db.collection("Users")
    val userDocRef = collectionRef.document(firebaseAuth.currentUser.uid)
    val profilePicFileName = "profilePic.png"
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageRef: StorageReference =
        storageRef.child(firebaseAuth.currentUser.uid + "/images/")

    lateinit var profilePicImg: ShapeableImageView
    lateinit var editProfilePicIcon: ShapeableImageView
    lateinit var nameAndAgeTextView: TextView
    lateinit var descriptionEditText: EditText
    lateinit var editDescriptionIcon: ImageView
    lateinit var educationTextView: TextView
    lateinit var genderTextView: TextView
    lateinit var editPassionsChipGroup: ChipGroup
    lateinit var editPassionsIcon: ImageView
    lateinit var profileProgressBar: ProgressBar
    lateinit var logOutBtn: Button
    lateinit var fragmentActionListener: FragmentActionListener
    lateinit var profilePicFile: File
    lateinit var picUri: Uri
    lateinit var firebaseProfilePicUrl: String
    lateinit var name: String
    lateinit var dob: String
    lateinit var gender: String
    lateinit var description: String
    lateinit var imageUrl: String
    lateinit var education: String
    lateinit var passions: List<String>

    val getCapturedImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                Log.e("callback", "image uri saved! " + picUri.toString())
                profilePicImg.setImageURI(picUri)
                updatePfp()
            } else {
                Log.e("callback", "failed!")
            }
        }

    val getGalleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            Log.e("gallery url", it.toString())
            profilePicImg.setImageURI(it)
            picUri = it
            updatePfp()
        } catch (e: Exception) {
            Log.e("callback", "failed!")
            e.printStackTrace()
        }
    }

    private fun updatePfp() {
        val profilePicRef = imageRef.child("profilePic.png")
        profilePicRef.putFile(picUri).addOnSuccessListener {
            Log.e("Profile picture", "updated!")
            profilePicRef.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseProfilePicUrl = it.result.toString()
                    updateUserPfpUrl(firebaseProfilePicUrl)
                    val sharedPref =
                        requireActivity().getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString(PROFILE_PIC_URL, firebaseProfilePicUrl)
                    editor.apply()
                } else {
                    Log.e("download url", "fetch failed :(")
                }
            }
        }.addOnFailureListener {
            it.printStackTrace()
            Toast.makeText(
                activity,
                "Error,profile picture couldn't be updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUserPfpUrl(firebaseProfilePicUrl: String) {
        userDocRef.update("imageURL", firebaseProfilePicUrl).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.e("pfp update url", "success!")
            } else {
                Log.e("pfp update url", "failure :(")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        Log.e("ProfileFragment", "created")
        Log.e("ProfileFragment onCreateView", "called!")
        return view
    }

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ProfileFragment onViewCreated", "called!")
        profilePicImg = view.findViewById(R.id.editProfilepicImageView)
        editProfilePicIcon = view.findViewById(R.id.edit_pfp_icon)
        nameAndAgeTextView = view.findViewById(R.id.name_age_txt_view)
        descriptionEditText = view.findViewById(R.id.edit_description_txt_view)
        editDescriptionIcon = view.findViewById(R.id.editConfirm_icon_img_view)
        educationTextView = view.findViewById(R.id.edu_txt_view)
        genderTextView = view.findViewById(R.id.my_gender_txt_view)
        editPassionsChipGroup = view.findViewById(R.id.edit_passion_chip_group)
        editPassionsIcon = view.findViewById(R.id.editConfirm_passion_icon)
        profileProgressBar = view.findViewById(R.id.profile_progress_bar)
        logOutBtn = view.findViewById(R.id.log_out_btn)

        logOutBtn.setOnClickListener {
            Log.e("Click", "log out btn")
            (requireActivity() as MainHostActivity2).logOut()
        }

        fetchDataFromUserDocument()
        editProfile()
    }

    private fun editProfile() {
        editDescriptionIcon.setOnClickListener {
            if (editDescriptionIcon.tag.equals("check")) {
                Log.e("check", "called")
                editDescriptionIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_edit
                    )
                )
                editDescriptionIcon.tag = "edit"
                val imm: InputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(descriptionEditText.windowToken, 0)
                disableEditText()
                if (!descriptionEditText.text.toString().trim().equals(description)) {
                    userDocRef.update("description", descriptionEditText.text.toString().trim())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.e("User description", "updated!")
                                description = descriptionEditText.text.toString().trim()
                            } else {
                                Log.e(
                                    "User description",
                                    "update failed :( ${it.exception.toString()}"
                                )
                            }
                        }
                } else {
                    Log.e("User description", "is the same,no update")
                }
            } else if (editDescriptionIcon.tag.equals("edit")) {
                Log.e("edit", "called")
                description = descriptionEditText.text.toString().trim()
                editDescriptionIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_check
                    )
                )
                editDescriptionIcon.tag = "check"
                enableEditText()
                descriptionEditText.requestFocus()
                val imm: InputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(descriptionEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        editPassionsIcon.setOnClickListener {
            if (this::fragmentActionListener.isInitialized) {
                val bundle = Bundle()
                bundle.putInt(ACTION_KEY, ACTION_EDIT_PASSIONS)
                val passionsArrayList: ArrayList<String> = arrayListOf()
                passionsArrayList.addAll(passions)
                bundle.putStringArrayList(USER_PASSIONS, passionsArrayList)
                fragmentActionListener.onActionCallBack(bundle)
            } else {
                Log.e("fragmentActionListener", "is not initialized in profile fragment")
            }
        }

        editProfilePicIcon.setOnClickListener {
            if (this::fragmentActionListener.isInitialized) {
                val bundle = Bundle()
                bundle.putInt(ACTION_KEY, ACTION_EDIT_PFP)
                fragmentActionListener.onActionCallBack(bundle)
            } else {
                Log.e("fragmentActionListener", "is not initialized in profile fragment")
            }
        }
    }

    private fun disableEditText() {
        descriptionEditText.isFocusable = false
        descriptionEditText.isFocusableInTouchMode = false
        descriptionEditText.inputType = InputType.TYPE_NULL
        descriptionEditText.isEnabled = false
    }

    private fun enableEditText() {
        descriptionEditText.isFocusable = true
        descriptionEditText.isFocusableInTouchMode = true
        descriptionEditText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        descriptionEditText.isSingleLine = false
        descriptionEditText.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        descriptionEditText.isEnabled = true
    }

    private fun fetchDataFromUserDocument() {
        userDocRef.get().addOnSuccessListener {
            name = it.getString("name")!!.trim()
            dob = it.getString("dob")!!.trim()
            gender = it.getString("gender")!!.trim()
            description = it.getString("description") ?: ""
            imageUrl = it.getString("imageURL") ?: ""
            education = it.getString("insitute")!!
            passions = it.get("passions") as List<String>
            populateProfilePage()
        }.addOnFailureListener {
            it.printStackTrace()
            Log.e("User doc fetch", it.toString())
        }
    }

    private fun populateProfilePage() {
        if (!imageUrl.equals("")) {
            Glide.with(profilePicImg.context)
                .load(imageUrl)
                .placeholder(R.drawable.blankplaceholder)
                .error(R.drawable.brokenplaceholder)
                .fallback(R.drawable.brokenplaceholder)
                .into(profilePicImg)
        }
        descriptionEditText.setText(description)
        val index1 = dob.indexOf('/')
        val index2 = dob.lastIndexOf('/')
        val day = dob.substring(0, index1).toInt()
        val month = dob.substring(index1 + 1, index2).toInt()
        val year = dob.substring(index2 + 1).toInt()
        val userAge = getAge(year, month - 1, day)
        nameAndAgeTextView.text = "${name}, ${userAge}"
        educationTextView.text = "  Studies at ${education}"
        genderTextView.text = "  ${gender}"

        for (index in passions.indices) {
            val chip = Chip(activity)
            chip.text = passions[index]
            chip.tag = passions[index]
            editPassionsChipGroup.addView(chip)
        }
        profileProgressBar.visibility = View.GONE
        //val img=ContextCompat.getDrawable(requireActivity(), R.drawable.ic_education)
    }

    private fun getAge(year: Int, month: Int, day: Int): String {
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    fun deleteUserPfp() {
        userDocRef.update("imageURL", "").addOnCompleteListener {
            if (it.isSuccessful) {
                Log.e("pfp deletion", "success!")
                profilePicImg.setImageResource(R.drawable.blankuser)
            } else {
                Log.e("pfp deletion", "failure :(")
            }
        }
    }

    fun captureImageForPfp() {
        profilePicFile = File(requireActivity().filesDir, profilePicFileName)
        picUri =
            FileProvider.getUriForFile(requireActivity(), "com.myapp.travelize", profilePicFile)
        try {
            getCapturedImage.launch(picUri)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.printStackTrace()
        }
    }

    fun getImageFromGalleryForPfp() {
        getGalleryImage.launch("image/*")
    }

    @SuppressLint("LongLogTag")
    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("ProfileFragment onDestroyView", "called!")
    }
}