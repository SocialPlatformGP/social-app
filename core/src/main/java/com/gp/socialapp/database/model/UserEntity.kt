package com.gp.socialapp.database.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    val userFirstName: String,
    val userLastName: String,
    val userPassword: String,
    val userProfilePictureURL: String,
    val userBirthdate: Date,
    @PrimaryKey
    val userEmail: String,
    val userPhoneNumber: String,
    val userBio: String,
    val userCreatedAt: Date,
    val administration: String="",

):Serializable
{
    constructor():this("","","","",Date(),"","","",Date())
}