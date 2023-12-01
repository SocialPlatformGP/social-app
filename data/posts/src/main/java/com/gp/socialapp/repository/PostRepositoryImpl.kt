package com.gp.socialapp.repository

import android.util.Log
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.source.local.PostLocalDataSource
import com.gp.socialapp.source.remote.PostRemoteDataSource
import com.gp.socialapp.util.PostMapper.toEntity
import com.gp.socialapp.util.PostMapper.toModel
import com.gp.socialapp.util.PostMapper.toNetworkModel
import com.gp.socialapp.util.PostMapper.toPostFlow
import com.gp.socialapp.utils.NetworkStatus
import com.gp.socialapp.utils.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postLocalSource: PostLocalDataSource,
    private val postRemoteSource: PostRemoteDataSource,
    private val networkStatus: NetworkStatus,
    private val repositoryScope: CoroutineScope
) : PostRepository {
    override suspend fun insertLocalPost(vararg post: PostEntity) {
        postLocalSource.insertPost(*post)
    }

    override suspend fun updateLocalPost(post: Post) {
        postLocalSource.updatePost(post)
    }

    override fun getAllLocalPosts(): Flow<List<Post>> {
        if (networkStatus.isOnline()) {
            val posts = postRemoteSource.fetchPosts()
            repositoryScope.launch {
                postLocalSource.deleteAllPosts()
                posts.collect {
                    it.forEach { post ->
                        insertLocalPost(post.toEntity())
                    }
                }
            }
            return posts
        } else {
            return postLocalSource.getAllPosts().toPostFlow()
        }
    }

    override suspend fun deleteLocalPost(post: Post) {
        postLocalSource.deletePost(post)
    }


    override fun fetchNetworkPosts(): Flow<List<Post>> {
        return postRemoteSource.fetchPosts()
    }

    override suspend fun updatePost(post: Post) {
        postRemoteSource.updatePost(post)
    }

    override suspend fun deletePost(post: Post) {
        postRemoteSource.deletePost(post)
    }

    override fun createPost(post: Post) = postRemoteSource.createPost(post)

    override fun onCleared() {
        repositoryScope.cancel()
    }

    override fun searchPostsByTitle(searchText: String): Flow<List<Post>> {
        return postLocalSource.searchPostsByTitle(searchText).toPostFlow()
    }

    override suspend fun upVotePost(post: Post) = postRemoteSource.upVotePost(post)

    override suspend fun downVotePost(post: Post) = postRemoteSource.downVotePost(post)
    override fun fetchPostById(id: String): Flow<Post> {
        if (networkStatus.isOnline()) {
            val post = postRemoteSource.fetchPostById(id)
            repositoryScope.launch {
                post.collect {
                    insertLocalPost(it.toEntity())
                }
            }
            return post
        }
        else{
           return postLocalSource.getPostById(id).toModel()
        }
    }

    override fun deleteAllPosts() {

    }


    override suspend fun incrementReplyCounter(postId: String) = postRemoteSource.incrementReplyCounter(postId)

    override suspend fun decrementReplyCounter(postId: String) = postRemoteSource.decrementReplyCounter(postId)
    override fun getAllTags()=postRemoteSource.getAllTags()

    override suspend fun insertTag(tag: Tag) = postRemoteSource.insertTag(tag)

}