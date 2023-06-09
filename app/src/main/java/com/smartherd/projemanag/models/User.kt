package com.smartherd.projemanag.models

import android.os.Parcel
import android.os.Parcelable

// In order to make this class parcelable
// 1. File => 2. Settings => 3. Plugins..under plugins search for parcelable => 4. Install Android Parcelable code generator
data class User (
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "", // Profile Picture...image is a string since the image will be stored as a link to the appropriate location
    val mobile: Long = 0,
    val fcmToken: String = "", // Used to know the specific user that is logged in so that notifications can be sent to him directly
    var selected : Boolean = false // Parcelable implementation not used
) : Parcelable
/** Everything under this has been automatically generated by the Parcelable plugin **/
{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeLong(mobile)
        parcel.writeString(fcmToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}