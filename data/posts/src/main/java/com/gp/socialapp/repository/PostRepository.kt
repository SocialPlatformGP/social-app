package com.gp.socialapp.repository

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun insertLocalPost(vararg post: PostEntity)
    suspend fun updateLocalPost(post: PostEntity)
     fun getAllLocalPosts(): Flow<List<PostEntity>>
    suspend fun deleteLocalPost(post: PostEntity)
     fun fetchNetworkPosts(): Flow<List<PostEntity>>
    suspend fun updatePost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
    fun createPost(post: Post): Flow<State<Nothing>>
    fun onCleared()
}