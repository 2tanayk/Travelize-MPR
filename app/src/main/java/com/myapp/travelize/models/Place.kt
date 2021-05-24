package com.myapp.travelize.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Place(
    var id: String? = null,
    var name: String? = null,
    var photoRef: String? = null,
    var address: String? = null,
    var rating: Double? = null,
    var totalRatings: Int,
    var phoneNo: String? = null,
    var workingHours: MutableList<String> = mutableListOf(),
    var isExpanded: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readInt(),
        parcel.readString(),
        parcel.createStringArrayList()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(photoRef)
        parcel.writeString(address)
        parcel.writeValue(rating)
        parcel.writeInt(totalRatings)
        parcel.writeString(phoneNo)
        parcel.writeStringList(workingHours)
        parcel.writeByte(if (isExpanded) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }
}
