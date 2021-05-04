package com.myapp.travelize.adapters.utils

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.myapp.travelize.models.Place

class PlaceDiffUtil : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        Log.e("areItemsTheSame","invoked!")
        return oldItem.id.equals(newItem.id)
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        Log.e("areContentsTheSame","invoked!")
        return oldItem.address.equals(newItem.address) &&
                oldItem.name.equals(newItem.name) &&
                oldItem.phoneNo.equals(newItem.phoneNo) &&
                oldItem.rating == newItem.rating &&
                oldItem.workingHours == newItem.workingHours &&
                oldItem.photoRef.equals(newItem.photoRef) &&
                oldItem.isExpanded == newItem.isExpanded

    }
}