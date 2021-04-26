package com.myapp.travelize.adapters

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.bold
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.travelize.Keys
import com.myapp.travelize.R
import com.myapp.travelize.adapters.utils.PlaceDiffUtil
import com.myapp.travelize.models.Place


class PlaceAdapter(val listener: OnItemClickListener) :
    ListAdapter<Place, PlaceAdapter.PlaceViewHolder>(PlaceDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val placeItemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.place_item, parent, false)

        return PlaceViewHolder(placeItemView)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        Log.e("onBindViewHolder", "I'm called")
        val place = getItem(position)
        val nameText = place.name
        val index = nameText?.indexOf('(')
        if (index != -1) {
            holder.nameTxtView.text = place.name?.substring(0, nameText!!.indexOf('('))
        } else {
            holder.nameTxtView.text = nameText
        }
        Glide.with(holder.placeImgView.getContext())
            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${place.photoRef.toString()}&key=${Keys.apiKey()}")
            .placeholder(R.drawable.blankplaceholder)
            .error(R.drawable.brokenplaceholder).centerInside()
            .fallback(R.drawable.brokenplaceholder).centerInside()
            .into(holder.placeImgView)
//        Log.e("Image ref",place.photoRef.toString())
        val address = " ${place.address}"
        holder.addressTxtView.text = address
        holder.phonenoTxtView.text = if (place.phoneNo != null) {
            "  ${place.phoneNo}"
        } else {
            "Not found"
        }
        //holder.workhrTxtView.text = workingHrs.substring(1, workingHrs.length - 1)
        val spanStr = SpannableStringBuilder().bold { append("Working Hours:") }
        holder.workhrTxtView.text = spanStr
        val workingHrs = place.workingHours
        for (workingHr in workingHrs) {
            holder.workhrTxtView.append("\n${workingHr}")
        }
        holder.placeRatingBar.rating = place.rating?.toFloat() ?: 0.0f
        holder.totalRatingsTxtView.text = "(${place.totalRatings})"
        val isExpanded = place.isExpanded
        holder.expandLL.visibility = if (isExpanded) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    inner class PlaceViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val placeImgView: ImageView = view.findViewById(R.id.place_image_view)
        val nameTxtView: TextView = view.findViewById(R.id.place_title_text_view)
        val appendBtn: ImageView = view.findViewById(R.id.append_image_btn)
        val expandLL: ConstraintLayout = view.findViewById(R.id.expandable_layout)
        val addressTxtView: TextView = view.findViewById(R.id.place_address_text_view)
        val phonenoTxtView: TextView = view.findViewById(R.id.place_no_text_view)
        val workhrTxtView: TextView = view.findViewById(R.id.place_workhours_text_view)
        val placeRatingBar: RatingBar = view.findViewById(R.id.place_rating_bar)
        val totalRatingsTxtView: TextView = view.findViewById(R.id.total_ratings_text_view)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}