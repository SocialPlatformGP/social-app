package com.gp.socialapp.source.remote

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import kotlinx.coroutines.flow.Flow

interface PostRemoteDataSource {
    suspend fun createPost(post: NetworkPost)
     fun fetchPosts(): Flow<List<PostEntity>>
    suspend fun updatePost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
}