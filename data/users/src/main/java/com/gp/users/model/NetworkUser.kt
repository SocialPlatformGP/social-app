package com.gp.users.model

import java.util.Date

data class NetworkUser(
    val userFirstName: String,
    val userLastName: String,
    val userPassword: String,
    val userProfilePictureURL: String,
    val userEmail: String,
    val userPhoneNumber: String,
    val userBirthdate: Date,
    val userBio: String,
    val userCreatedAt: Date
    ){
    constructor():this("","","","","","",Date(),"",Date())
}
