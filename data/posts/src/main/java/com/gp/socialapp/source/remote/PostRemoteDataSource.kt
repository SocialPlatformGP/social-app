package com.gp.socialapp.source.remote

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface PostRemoteDataSource {
    fun createPost(post: Post): Flow<State<Nothing>>
    fun fetchPosts(): Flow<List<Post>>
    suspend fun updatePost(post: Post)
    suspend fun deletePost(post: Post)
    suspend fun upVotePost(post: Post)
    suspend fun downVotePost(post: Post)
    fun fetchPostById(id: String): Flow<Post>
    suspend fun incrementReplyCounter(postId: String)
    suspend fun decrementReplyCounter(postId: String)

    fun getAllTags(): Flow<List<Tag>>
    suspend fun insertTag(tag: Tag)
}