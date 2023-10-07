package com.gp.socialapp.source.remote

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost

interface PostRemoteDataSource {
    suspend fun createPost(post: NetworkPost)
    suspend fun fetchPosts(): List<NetworkPost>
    suspend fun updatePost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
}