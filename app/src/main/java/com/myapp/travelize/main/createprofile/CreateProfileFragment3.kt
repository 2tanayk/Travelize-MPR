package com.myapp.travelize.main.createprofile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CreateProfileFragment3 : androidx.fragment.app.Fragment() {
    var uri: Uri? = null
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUST_IMAGE_GALLERY = 2
    val picDirName = "profilePicDir"
    val profilePicFileName = "profilePic.png"

    val getCapturedImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                Log.e("callback", "image uri saved! " + uri.toString())
                profilePicImageView.setImageURI(uri)
            } else {
                Log.e("callback", "failed!")
            }
        }
    lateinit var imagePath: String
    lateinit var uploadBtn: Button
    lateinit var context: MainHostActivity
    lateinit var profilePicImageView: ImageView
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
                    }
                }
                .show()
        }
    }

    private fun createImageFile() {
//
//        val contextWrapper = ContextWrapper(context.applicationContext)
//        val directory = contextWrapper.getDir(picDirName, Context.MODE_PRIVATE)
//        val directory = context.getDir(picDirName, Context.MODE_PRIVATE)
        val profilePicFile = File(context.filesDir, profilePicFileName)

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

        uri = FileProvider.getUriForFile(
            context,
            "com.myapp.travelize",
            profilePicFile
        )
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            getCapturedImage.launch(uri)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.printStackTrace()
        }
    }
}