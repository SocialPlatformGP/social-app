package com.gp.socialapp.source.remote

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface PostRemoteDataSource {
    fun createPost(post: NetworkPost): Flow<State<Nothing>>
    fun fetchPosts(): Flow<List<PostEntity>>
    suspend fun updatePost(post: PostEntity)
    suspend fun deletePost(post: PostEntity)
}