package com.myapp.travelize.main.createprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity

class CreateProfileFragment2 : Fragment() {
    lateinit var passionChipGroup: ChipGroup
    lateinit var nextBtn2: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val passionSet: MutableSet<String> = mutableSetOf()
        val context = activity as? MainHostActivity
        val passions = listOf<String>(
            "Cycling",
            "Jogging",
            "Dancing",
            "Drawing",
            "Football",
            "Music",
            "Climbing",
            "Stand up Comedy",
            "Cooking",
            "Swimming"
        )
        passionChipGroup = view.findViewById(R.id.passionChipGroup)
        nextBtn2 = view.findViewById(R.id.continueBtn2)
        for (index in passions.indices) {
            val chip = Chip(activity)
            chip.text = passions[index]
            chip.tag = passions[index]
            // necessary to get single selection working
            chip.isClickable = true
            chip.checkedIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_chipcheck)
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            passionChipGroup.addView(chip)
        }

        nextBtn2.setOnClickListener {
//            Log.e("Selected chip id", passionChipGroup.checkedChipId.toString())
            Log.e("Selected chips", passionChipGroup.checkedChipIds.toString())

            if (passionChipGroup.checkedChipIds.size < 5) {
                Toast.makeText(activity, "Select at least 5 :)", Toast.LENGTH_SHORT).show()
            } else {
                passionChipGroup
                    .children
                    .toList()
                    .filter { (it as Chip).isChecked }
                    .forEach {
                        passionSet.add((it as Chip).text.toString())
                    }
                Log.e("User passions", passionSet.toString())
                context?.saveSharedPrefs(passionSet)
                context?.replaceFragment(CreateProfileFragment3())
            }
        }
    }
}