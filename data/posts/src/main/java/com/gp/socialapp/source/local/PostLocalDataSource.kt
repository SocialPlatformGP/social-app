package com.gp.socialapp.source.local
import com.gp.socialapp.database.model.PostEntity
import kotlinx.coroutines.flow.Flow

interface PostLocalDataSource {
    suspend fun insertPost(vararg post: PostEntity)
    suspend fun updatePost(post: PostEntity)
     fun getAllPosts(): Flow<List<PostEntity>>
    suspend fun deletePost(post: PostEntity)
}