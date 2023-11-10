package com.gp.users.util

import com.gp.socialapp.database.model.UserEntity
import com.gp.users.model.NetworkUser
import com.gp.users.model.User
import java.util.Date

object UserMapper {
    fun NetworkUser.toEntity(): UserEntity {
        return UserEntity(userFirstName, userLastName, userPassword, userProfilePictureURL, userBirthdate,
                            userEmail, userPhoneNumber, userBio, userCreatedAt)
    }
    fun UserEntity.toNetworkModel(): NetworkUser{
        return NetworkUser(userFirstName,userLastName,userPassword,userProfilePictureURL,userEmail,
            userPhoneNumber, userBirthdate, userBio, userCreatedAt)
    }
    fun NetworkUser.toModel(): User {
        return User(
            firstName = userFirstName,
            lastName = userLastName,
            profilePictureURL = userProfilePictureURL,
            phoneNumber = userPhoneNumber,
            email = userEmail,
            birthdate =userBirthdate,
            bio = userBio
        )
    }
}