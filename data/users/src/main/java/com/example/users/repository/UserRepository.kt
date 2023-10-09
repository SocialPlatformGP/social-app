package com.example.users.repository

import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insertUser(userEntity: UserEntity)

    suspend fun updateUser(userEntity: UserEntity)

    suspend fun deleteUser(userEntity: UserEntity)

    suspend fun getAllUsers(): Flow<List<UserEntity>>

    suspend fun getUserByID(userID: String)
}