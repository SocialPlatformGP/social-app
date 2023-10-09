package com.gp.socialapp.repository

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import com.gp.socialapp.source.local.PostLocalDataSource
import com.gp.socialapp.source.remote.PostRemoteDataSource
import com.gp.socialapp.util.PostMapper.toNetworkModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor (
    private val postLocalSource: PostLocalDataSource,
    private val postRemoteSource: PostRemoteDataSource)
    : PostRepository {
    private val currentUserID = 1L
    override suspend fun insertLocalPost(vararg post: PostEntity) {
        postLocalSource.insertPost(*post)
    }

    override suspend fun updateLocalPost(post: PostEntity) {
        postLocalSource.updatePost(post)
    }

    override suspend fun getAllLocalPosts(): List<PostEntity> {
        return postLocalSource.getAllPosts()
    }

    override suspend fun deleteLocalPost(post: PostEntity) {
        postLocalSource.deletePost(post)
    }

    override suspend fun createNetworkPost(post: NetworkPost) {
        postRemoteSource.createPost(post)
    }

    override suspend fun fetchNetworkPosts(): List<NetworkPost> {
        return postRemoteSource.fetchPosts()
    }

    override suspend fun updatePost(post: PostEntity) {
        postRemoteSource.updatePost(post)
    }

    override suspend fun deletePost(post: PostEntity) {
        postRemoteSource.deletePost(post)
    }
    override suspend fun createPost(post: Post){
        createNetworkPost(post.toNetworkModel(currentUserID))
    }
}