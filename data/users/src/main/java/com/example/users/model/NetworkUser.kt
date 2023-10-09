package com.example.users.model

data class NetworkUser(
    val userName: String,
    val userPassword: String,
    val userProfilePictureURL: String,
    val userEmail: String,
    val userPhoneNumber: String,
    val userBookmarks: List<String>)
