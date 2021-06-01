package com.myapp.travelize.main.createprofile


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.travelize.Constants.Companion.ACTION_EDIT_PASSIONS
import com.myapp.travelize.Constants.Companion.ACTION_KEY
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.main.MainHostActivity.Companion.USER_PASSIONS


class CreateProfileFragment2 : Fragment() {
    lateinit var passionChipGroup: ChipGroup
    lateinit var labelTextView: TextView
    lateinit var subLabelTextView: TextView
    lateinit var nextBtn2: MaterialButton
    lateinit var passions: List<String>
    lateinit var toolbar: Toolbar
    lateinit var toolbarTextView: TextView
    lateinit var v: View
    var updatedPassionsArray: ArrayList<String> = arrayListOf()
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val collectionRef = db.collection("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        nextBtn2 = view.findViewById(R.id.continueBtn2)
        passionChipGroup = view.findViewById(R.id.passionChipGroup)
        toolbar = view.findViewById(R.id.edit_passions_toolbar)
        passions = listOf<String>(
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

        val bundle = arguments
        if (bundle != null) {
            val performedAction = bundle.getInt(ACTION_KEY, -1)
            if (performedAction == ACTION_EDIT_PASSIONS) {
                val passionsArrayList = bundle.getStringArrayList(USER_PASSIONS)
                editPassions(passionsArrayList)
            }
        } else {
            createPassions()
        }
    }

    private fun createPassions() {
        val passionSet: MutableSet<String> = mutableSetOf()
        val context = activity as? MainHostActivity

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

    private fun editPassions(passionsArrayList: ArrayList<String>?) {
        val tempArrayList: ArrayList<String> = arrayListOf()
        if (passionsArrayList != null) {
            tempArrayList.addAll(passionsArrayList)
        }
        labelTextView = v.findViewById(R.id.passionsLabelTextView)
        labelTextView.visibility = View.GONE
        subLabelTextView = v.findViewById(R.id.subLabelTextView)
        subLabelTextView.visibility = View.GONE
        nextBtn2.text = "Save"
        nextBtn2.visibility = View.GONE
        toolbar.visibility = View.VISIBLE

        activity?.setActionBar(toolbar)

//        val toolbarTitle = SpannableStringBuilder("Edit Your Passions")
//        toolbarTitle.setSpan(StyleSpan(Typeface.BOLD), 0, toolbarTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        toolbar.title =toolbarTitle
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarTextView = toolbar.findViewById(R.id.toolbarTextView)
        toolbarTextView.visibility = View.VISIBLE
        toolbarTextView.text = "Edit Your Passions"

        toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        for (index in passions.indices) {
            val chip = Chip(activity)
            chip.text = passions[index]
            chip.tag = passions[index]

            // necessary to get single selection working
            chip.isClickable = true
            chip.checkedIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_chipcheck)
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            if (passionsArrayList != null) {
                if (passionsArrayList.contains(passions[index])) {
                    Log.e("passion", passions[index])
                    chip.isChecked = true
                }
            } else {
                Log.e("passionsArraylist", "is null")
                return
            }
            chip.setOnCheckedChangeListener { compoundButton, b ->
                Log.e("chip ${chip.tag}", b.toString())
                if (!b) {
                    tempArrayList.remove(chip.tag.toString())
                } else {
                    tempArrayList.add(chip.tag.toString())
                }
                if (passionsArrayList.size == tempArrayList.size && passionsArrayList.filter { !tempArrayList.contains(it) }.isEmpty()) {
                    Log.e("nextBtn2","GONE")
                    val animSlideDown: Animation =
                        AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_down)
                    nextBtn2.startAnimation(animSlideDown)
                    clearButtonAnimation()
                    nextBtn2.visibility = View.GONE
                } else {
                    Log.e("nextBtn2","VISIBLE")
                    if(!nextBtn2.isVisible) {
                        nextBtn2.visibility = View.VISIBLE
                        val animSlideUp: Animation =
                            AnimationUtils.loadAnimation(requireActivity(), R.anim.slide_up)
                        nextBtn2.startAnimation(animSlideUp)
                        clearButtonAnimation()
                    }
                    updatedPassionsArray.clear()
                    updatedPassionsArray.addAll(tempArrayList)
                }
            }
            passionChipGroup.addView(chip)
        }

        nextBtn2.setOnClickListener {
            collectionRef.document(firebaseAuth.currentUser.uid).update("passions",updatedPassionsArray).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e("Passions field","updated!")
                    Toast.makeText(requireActivity(),"Saved!",Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.popBackStack()
                }else{
                    Log.e("Passions field","update failed :(")
                }
            }
        }
    }

    private fun clearButtonAnimation() {
        Handler(Looper.getMainLooper()).postDelayed({
            nextBtn2.clearAnimation()
        }, 300)
    }
}