package com.smartherd.projemanag.models

import android.os.Parcel
import android.os.Parcelable

// TODO Adding Cards to Lists (Step 1: Create a data model class for CARD.)
data class Card(
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    // TODO Setting the Color and Updating the Card  (Step 5: Add a field for label color.)
    var labelColor: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(createdBy)
        dest.writeStringList(assignedTo)
        dest.writeString(labelColor)
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
