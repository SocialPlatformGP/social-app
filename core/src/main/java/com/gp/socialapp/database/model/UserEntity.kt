package com.gp.socialapp.database.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userID: String,
    val userName: String,
    val userPassword: String,
    val userProfilePictureURL: String,
    val userEmail: String,
    val userPhoneNumber: String,
)