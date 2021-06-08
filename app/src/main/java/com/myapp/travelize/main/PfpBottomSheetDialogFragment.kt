package com.myapp.travelize.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.travelize.Constants
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP_CAMERA
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP_DELETE
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PFP_GALLERY
import com.myapp.travelize.Constants.Companion.ACTION_KEY
import com.myapp.travelize.R
import com.myapp.travelize.interfaces.FragmentActionListener

class PfpBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {

        const val TAG = "PfpBottomSheetDialogFragment"

    }
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val collectionRef = db.collection("Users")
    val userDocRef = collectionRef.document(firebaseAuth.currentUser.uid)
    lateinit var fragmentActionListener: FragmentActionListener

    lateinit var deletePfpIcon: ImageView
    lateinit var galleryPfpIcon: ImageView
    lateinit var cameraPfpIcon: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_pfp_modal_bottom_sheet, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deletePfpIcon=view.findViewById(R.id.delete_pfp_icon)
        galleryPfpIcon=view.findViewById(R.id.gallery_pfp_icon)
        cameraPfpIcon=view.findViewById(R.id.camera_pfp_icon)

        deletePfpIcon.setOnClickListener {
            Log.e("parent activity", (activity as MainHostActivity2).toString())
            if(this::fragmentActionListener.isInitialized){
                val bundle=Bundle()
                bundle.putInt(ACTION_KEY, ACTION_EDIT_PFP_DELETE)
                fragmentActionListener.onActionCallBack(bundle)
            }else{
                Log.e("fragmentActionListener","is not initialized in pfp bottom sheet")
            }
            dismiss()
        }

        galleryPfpIcon.setOnClickListener {
            if(this::fragmentActionListener.isInitialized){
                val bundle=Bundle()
                bundle.putInt(ACTION_KEY, ACTION_EDIT_PFP_GALLERY)
                fragmentActionListener.onActionCallBack(bundle)
            }else{
                Log.e("fragmentActionListener","is not initialized in pfp bottom sheet")
            }
            dismiss()
        }

        cameraPfpIcon.setOnClickListener {
            if(this::fragmentActionListener.isInitialized){
                val bundle=Bundle()
                bundle.putInt(ACTION_KEY, ACTION_EDIT_PFP_CAMERA)
                fragmentActionListener.onActionCallBack(bundle)
            }else{
                Log.e("fragmentActionListener","is not initialized in pfp bottom sheet")
            }
            dismiss()
        }
    }

}