package com.example.users.Source.remote

import com.example.users.model.NetworkUser
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    suspend fun createPost(user: NetworkUser)
    suspend fun updatePost(user: UserEntity)
    suspend fun fetchPosts(): Flow<List<NetworkUser>>
    suspend fun deletePost(user: UserEntity)
}