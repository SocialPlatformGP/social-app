package com.example.posts.source.local


import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply
import com.gp.socialapp.models.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow

interface PostLocalDataSource {

    suspend fun insertPost(post: Post): Long
    suspend fun insertPosts(posts: List<Post>)
    suspend fun insertReply(reply: Reply): Long
    suspend fun insertReplies(replies: List<Reply>)
    suspend fun updatePost(post: Post)
    suspend fun updatePosts(posts: List<Post>)
    suspend fun updateReply(reply: Reply)
    suspend fun updateReplies(replies: List<Reply>)
    suspend fun deletePost(post: Post)
    suspend fun deletePosts(posts: List<Post>)
    suspend fun deleteReply(reply: Reply)
    suspend fun deleteReplies(replies: List<Reply>)
    suspend fun deleteAllPosts()
    suspend fun deleteAllReplies()
    fun getAllPosts(): Flow<List<Post>>
    fun getPostById(id: Long): Flow<Post>
    fun getAllReplies(): Flow<List<Reply>>
    fun getRepliesByPostId(postId: Long): Flow<List<Reply>>
    fun getReplyById(id: Long): Flow<Reply>
    fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>>
    fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>>

    fun getPostwithReplies(postId: Long): Flow<PostWithReplies>

    fun getAllPostswithReplies(): Flow<List<PostWithReplies>>


}