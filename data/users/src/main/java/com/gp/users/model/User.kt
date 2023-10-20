package com.gp.users.model

import java.util.Date

data class User(
    val firstName: String,
    val lastName: String,
    val profilePictureURL: String,
    val phoneNumber: String,
    val email: String,
    val birthdate: Date,
    val bio: String
)
