package com.myapp.travelize.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.myapp.travelize.R
import com.myapp.travelize.models.PlaceType


class PlaceTypeAdapter(context: Context, placeTypeList: ArrayList<PlaceType>) :
    ArrayAdapter<PlaceType>(context, 0, placeTypeList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cView: View =
            LayoutInflater.from(context).inflate(R.layout.type_list_item, parent, false)
        val placeIconImg: ImageView = cView.findViewById(R.id.place_type_icon)
        val placeNameTxt: TextView = cView.findViewById(R.id.place_type_txt)

        val currentPlaceItem=getItem(position)

        if (currentPlaceItem != null) {
            placeIconImg.setImageResource(currentPlaceItem.typeIcon)
            placeNameTxt.text=currentPlaceItem.typeName
        }
        return cView
    }

}