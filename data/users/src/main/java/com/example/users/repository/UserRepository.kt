package com.example.users.repository

import com.example.users.model.NetworkUser
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insertUser(userEntity: UserEntity)

    suspend fun updateUser(userEntity: UserEntity)

    suspend fun deleteUser(userEntity: UserEntity)

    suspend fun getAllUsers(): Flow<List<UserEntity>>

    suspend fun getUserByID(userID: String)
    suspend fun createPost(user: NetworkUser)
    suspend fun updatePost(user: UserEntity)
    suspend fun fetchPosts(): Flow<List<NetworkUser>>
    suspend fun deletePost(user: UserEntity)
}