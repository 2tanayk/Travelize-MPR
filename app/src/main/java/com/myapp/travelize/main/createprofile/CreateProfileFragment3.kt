package com.myapp.travelize.main.createprofile

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.main.MainHostActivity2
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CreateProfileFragment3 : androidx.fragment.app.Fragment() {
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var imageRef: StorageReference? =
        storageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/images/")


    var picUri: Uri? = null
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUST_IMAGE_GALLERY = 2
    val picDirName = "profilePicDir"
    val profilePicFileName = "profilePic.png"

    private val requestReadExternalStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.e("Permission", "Granted")
            } else {
                Log.e("Permission", "Denied")
            }
        }

    val getCapturedImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                Log.e("callback", "image uri saved! " + picUri.toString())
                profilePicImageView.setImageURI(picUri)
            } else {
                Log.e("callback", "failed!")
            }
        }
    val getGalleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            profilePicImageView.setImageURI(it)
            picUri = it
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    lateinit var firebaseProfilePicUrl: String
    lateinit var profilePicFile: File
    lateinit var imagePath: String
    lateinit var uploadBtn: Button
    lateinit var context: MainHostActivity
    lateinit var profilePicImageView: ImageView
    lateinit var nextBtn3: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val builder = VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context = activity as MainHostActivity
        profilePicImageView = view.findViewById(R.id.profilepicImageView)
        uploadBtn = view.findViewById(R.id.uploadImageBtn)
        uploadBtn.setOnClickListener {
//            Log.e("CreateProfileFragment3", "clicked!")
            val items = arrayOf("Take Photo", "Choose from Gallery")

            MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Add Photo")
                .setItems(items) { dialog, which ->
                    // Respond to item chosen
                    Log.e("Image pick dialog", which.toString())
                    if (which == 0) {
                        createImageFile()
                        dispatchTakePictureIntent()
                    } else {
//                        Log.e("Gallery", "oops clicked!")
                        if (!hasExternalStoragePermission()) {
                            requestReadExternalStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        if (hasExternalStoragePermission()) {
                            getGalleryImage.launch("image/*")
                        }
                    }
                }
                .show()
        }

        nextBtn3 = view.findViewById(R.id.continueBtn3)
        nextBtn3.setOnClickListener {
            Log.e("Result of safe cast", context.toString())
            context.saveInfo()

            val profilePicRef = imageRef?.child("profilePic.png")

            if (picUri != null) {
                profilePicRef
                    ?.putFile(picUri!!)
                    ?.addOnSuccessListener {
                        Toast.makeText(activity, "Profile picture added!", Toast.LENGTH_SHORT).show()
                        profilePicRef
                            .downloadUrl
                            .addOnSuccessListener {
                                Log.e("Firebase", "Download url saved!")
                                firebaseProfilePicUrl = it.toString()
                                context.saveSharedPrefs(firebaseProfilePicUrl)
                                val intent = Intent(activity, MainHostActivity2::class.java)
                                intent.putExtra("New User", true)
                                context.startActivity(intent)
                                context.finish()
                            }.addOnFailureListener {
                                Log.e("Firebase", "Download url error :(")
                                it.printStackTrace()
                            }
                    }
                    ?.addOnFailureListener {
                        it.printStackTrace()
                        Toast.makeText(activity, "Error,profile picture couldn't be added", Toast.LENGTH_SHORT).show()
                    }
            }//null check
        }
    }

    private fun createImageFile() {
//
//        val contextWrapper = ContextWrapper(context.applicationContext)
//        val directory = contextWrapper.getDir(picDirName, Context.MODE_PRIVATE)
//        val directory = context.getDir(picDirName, Context.MODE_PRIVATE)
        profilePicFile = File(context.filesDir, profilePicFileName)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.blankuser)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(profilePicFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            Log.e("Exception!", "failed")
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
//
        imagePath = profilePicFile.path
        Log.e("Profile pic path", imagePath)
//        uri = Uri.fromFile(profilePicFile)

        picUri = FileProvider.getUriForFile(
            context,
            "com.myapp.travelize",
            profilePicFile
        )
    }

    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            getCapturedImage.launch(picUri)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.printStackTrace()
        }
    }

    fun hasExternalStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

}