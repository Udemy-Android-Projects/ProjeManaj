package com.smartherd.projemanag.models

import android.os.Parcel
import android.os.Parcelable

// TODO The TaskListActivity (Step 1: Create a data model class for Task.)
data class Task(
    var title: String = "",
    val createdBy: String = "",
    // TODO Adding Cards to Lists (Step 2: Add one more parameter as a cards list using the card model class.)
    var cards : ArrayList<Card> = ArrayList() // Initialized directly with an empty array list
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!! // Since we will pass user defined types(Card)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeTypedList(cards)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
