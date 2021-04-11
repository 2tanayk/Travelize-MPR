package com.myapp.travelize.main.createprofile


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileFragment1 : Fragment() {
    lateinit var displayDob: TextView
    lateinit var radioGroup: RadioGroup
    lateinit var radioButton: RadioButton
    lateinit var nextBtn: MaterialButton
    lateinit var autoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_profile1, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = activity as? MainHostActivity
        var instituteName = ""
        var gender = "Male"

        displayDob = view.findViewById(R.id.dobTextView)
        radioGroup = view.findViewById(R.id.genderRadioGroup)
        nextBtn = view.findViewById(R.id.continueBtn)
        autoCompleteTextView = view.findViewById(R.id.instituteAutoCompleteTextView)

        displayDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val formatter = SimpleDateFormat("yyyy-MM-dd")


            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setTitleText("Select Date")
                .build()

            if (context != null) {
                datePickerDialog.show(context.supportFragmentManager, "datepicker")
            }

            datePickerDialog.addOnPositiveButtonClickListener {
                Log.e("Date", datePickerDialog.selection.toString())
                val simpleFormat = SimpleDateFormat("dd/mm/yyyy")

                val date = Date(datePickerDialog.selection!!)
                val dateString =
                    "${date.date}/${date.month + 1}/" + date.year.toString().substring(1)
                val reformatDate = simpleFormat.parse(dateString)
                Log.e("Date", date.date.toString())
                Log.e("Date", (date.month + 1).toString())
                Log.e("Date", date.year.toString().substring(1))
                Log.e("RF Date", reformatDate.toString())
                displayDob.text = datePickerDialog.headerText
            }
        }

        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            radioButton = view.findViewById(i)
            gender = radioButton.text.toString()
            Log.e("Radio Group", gender)
        }
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val adapter = ArrayAdapter(requireActivity(), R.layout.menu_list_item, items)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnClickListener {
            instituteName = autoCompleteTextView.text.toString()
            Log.e("ATV item", instituteName)
            autoCompleteTextView.setOnItemClickListener { adapterView, view, i, l ->
                instituteName = autoCompleteTextView.text.toString()
                Log.e("ATV itemin", instituteName)
            }
        }



        nextBtn.setOnClickListener {
            instituteName = autoCompleteTextView.text.toString()
            Log.e("ATV itemfinal", instituteName)
            Log.e("Final gender", gender)

            if (displayDob.text.isEmpty()) {
                Toast.makeText(activity, "Please add your DOB!", Toast.LENGTH_SHORT).show()
            } else if (instituteName.isEmpty()) {
                Toast.makeText(activity, "Please select your institute!", Toast.LENGTH_SHORT).show()
            } else {
                context?.replaceFragment(CreateProfileFragment2())
            }
        }
    }
}