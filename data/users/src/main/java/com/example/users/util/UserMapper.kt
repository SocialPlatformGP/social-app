package com.example.users.util

import com.example.users.model.NetworkUser
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.UserEntity

object UserMapper {
    fun NetworkUser.toEntity(id: String): UserEntity {
        return UserEntity(id,userName,userPassword, userProfilePictureURL, userEmail, userPhoneNumber)
    }
    fun UserEntity.toNetworkModel(): NetworkUser{
        return NetworkUser(
            userName,userPassword, userProfilePictureURL, userEmail, userPhoneNumber)
    }
}