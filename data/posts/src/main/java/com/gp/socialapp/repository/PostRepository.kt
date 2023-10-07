package com.gp.socialapp.repository

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun insertLocalPost(vararg post: PostEntity)
    suspend fun updateLocalPost(post: PostEntity)
    suspend fun getAllLocalPosts(): Flow<List<PostEntity>>
    suspend fun deleteLocalPost(post: PostEntity)
    suspend fun createNetworkPost(post: NetworkPost)
    suspend fun fetchNetworkPosts(): List<NetworkPost>
    suspend fun updatePost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
}