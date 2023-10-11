package com.gp.socialapp.repository

import android.util.Log
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import com.gp.socialapp.source.local.PostLocalDataSource
import com.gp.socialapp.source.remote.PostRemoteDataSource
import com.gp.socialapp.util.PostMapper.toNetworkModel
import com.gp.socialapp.utils.NetworkStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postLocalSource: PostLocalDataSource,
    private val postRemoteSource: PostRemoteDataSource,
    private val networkStatus: NetworkStatus,
    private val repositoryScope: CoroutineScope
) : PostRepository {
    private val currentUserID = 1L
    override suspend fun insertLocalPost(vararg post: PostEntity) {
        postLocalSource.insertPost(*post)
    }

    override suspend fun updateLocalPost(post: PostEntity) {
        postLocalSource.updatePost(post)
    }

    override fun getAllLocalPosts(): Flow<List<PostEntity>> {
        if (networkStatus.isOnline()) {
            repositoryScope.launch {
                fetchNetworkPosts().collect {
                    it.forEach { post ->
                        insertLocalPost(post)
                    }
                }
            }
            return postLocalSource.getAllPosts()
        } else {
            return postLocalSource.getAllPosts()
        }
    }
    override suspend fun deleteLocalPost(post: PostEntity) {
        postLocalSource.deletePost(post)
    }

    override suspend fun createNetworkPost(post: NetworkPost) {
        postRemoteSource.createPost(post)
    }

    override fun fetchNetworkPosts(): Flow<List<PostEntity>> {
        return postRemoteSource.fetchPosts()
    }

    override suspend fun updatePost(post: PostEntity) {
        postRemoteSource.updatePost(post)
    }

    override suspend fun deletePost(post: PostEntity) {
        postRemoteSource.deletePost(post)
    }

    override suspend fun createPost(post: Post) {
        createNetworkPost(post.toNetworkModel(currentUserID))
        repositoryScope.launch {
            fetchNetworkPosts().collect {
                it.forEach { post ->
                    insertLocalPost(post)
                }
            }
        }
    }

    override fun onCleared() {
        repositoryScope.cancel()
    }
}