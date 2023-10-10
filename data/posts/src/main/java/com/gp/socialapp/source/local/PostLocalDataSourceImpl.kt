package com.gp.socialapp.source.local

import com.gp.socialapp.database.dao.PostDao
import com.gp.socialapp.database.model.PostEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostLocalDataSourceImpl @Inject constructor(private val postDao: PostDao): PostLocalDataSource {
    override suspend fun insertPost(vararg post: PostEntity) {
        postDao.insertPost(*post)
    }

    override suspend fun updatePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    override suspend fun getAllPosts(): List<PostEntity> {
        return postDao.getAllPosts()
    }

    override suspend fun deletePost(post: PostEntity) {
        postDao.deletePost(post)
    }
}