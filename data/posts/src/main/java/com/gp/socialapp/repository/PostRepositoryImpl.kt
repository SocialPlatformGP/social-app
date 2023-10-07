package com.gp.socialapp.repository

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.source.local.PostLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor (private val postLocalSource: PostLocalDataSource)
    : PostRepository {
    override suspend fun insertLocalPost(vararg post: PostEntity) {
        postLocalSource.insertPost(*post)
    }

    override suspend fun updateLocalPost(post: PostEntity) {
        postLocalSource.updatePost(post)
    }

    override suspend fun getAllLocalPosts(): Flow<List<PostEntity>> {
        return postLocalSource.getAllPosts()
    }

    override suspend fun deleteLocalPost(post: PostEntity) {
        postLocalSource.deletePost(post)
    }

}