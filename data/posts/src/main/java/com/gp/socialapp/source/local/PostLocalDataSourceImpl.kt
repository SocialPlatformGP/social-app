package com.gp.socialapp.source.local

import android.util.Log
import com.gp.socialapp.database.dao.PostDao
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.PostMapper.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostLocalDataSourceImpl @Inject constructor(private val postDao: PostDao): PostLocalDataSource {
    override suspend fun insertPost(vararg post: PostEntity) {
        postDao.insertPost(*post)
        Log.d("edrees", "Local Post")
    }

    override suspend fun updatePost(post: Post) {
        postDao.updatePost(post.toEntity())
    }

    override  fun getAllPosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }

    override suspend fun deletePost(post: Post) {
        postDao.deletePost(post.toEntity())
    }

    override fun searchPostsByTitle(searchText: String): Flow<List<PostEntity>> {
        return postDao.searchPostsByTitle(searchText)
    }

    override fun getPostById(id: String): Flow<PostEntity> = postDao.getPostById(id)
}