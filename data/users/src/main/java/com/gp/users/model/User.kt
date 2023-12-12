package com.gp.users.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class User(
    val firstName: String="",
    val lastName: String="",
    val profilePictureURL: String=  "",
    val phoneNumber: String="",
    val email: String = "",
    val birthdate: Date = Date(),
    val bio: String="",
    val administration: String="",
): Parcelable
