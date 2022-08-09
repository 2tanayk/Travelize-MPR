package com.myapp.travelize.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.myapp.travelize.R
import java.util.*
import kotlin.collections.ArrayList

class ProfileDialogFragment : DialogFragment() {
    lateinit var pfp: ImageView
    lateinit var nameAgeGenderTxt: TextView
    lateinit var educationTxt: TextView
    lateinit var descTxt: TextView
    lateinit var passionsChipGroup: ChipGroup

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.dialog_fragment_profile, container, false)
        Log.e("Bundle", arguments.toString())

        pfp = rootView.findViewById(R.id.other_pfp_image)
        nameAgeGenderTxt = rootView.findViewById(R.id.name_age_gender_txt)
        educationTxt = rootView.findViewById(R.id.education_txt)
        descTxt = rootView.findViewById(R.id.desc_txt)
        passionsChipGroup = rootView.findViewById(R.id.passion_chip_group)

        val imageURL = arguments?.get("imageURL") as String
        val name = arguments?.get("name") as String
        val dob = arguments?.get("dob") as String

        val index1 = dob.indexOf('/')
        val index2 = dob.lastIndexOf('/')
        val day = dob.substring(0, index1).toInt()
        val month = dob.substring(index1 + 1, index2).toInt()
        val year = dob.substring(index2 + 1).toInt()
        val userAge = getAge(year, month - 1, day)

        val gender = arguments?.get("gender") as String
        val institute = arguments?.get("institute") as String
        val description = arguments?.get("description") as String
        val passionsList = arguments?.get("passions") as ArrayList<String>

        if (!imageURL.equals("")) {
            Glide.with(pfp.context)
                .load(arguments?.get("imageURL"))
                .placeholder(R.drawable.blankplaceholder)
                .error(R.drawable.brokenplaceholder)
                .fallback(R.drawable.brokenplaceholder)
                .into(pfp)
        }

        val nameAgeGender = name + ", " + userAge + " (" + gender + ")"
        nameAgeGenderTxt.text = nameAgeGender
        educationTxt.text = institute
        descTxt.text = description

        for (index in passionsList.indices) {
            val chip = Chip(activity)
            chip.text = passionsList[index]
            chip.tag = passionsList[index]
            passionsChipGroup.addView(chip)
        }

        return rootView
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
}