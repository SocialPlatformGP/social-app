package com.example.posts.repository

import com.example.posts.source.local.PostLocalDataSource
import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postLocalDataSource: PostLocalDataSource
) : PostRepository {
    override suspend fun insertPost(post: Post) = postLocalDataSource.insertPost(post)

    override suspend fun insertPosts(posts: List<Post>) = postLocalDataSource.insertPosts(posts)

    override suspend fun insertReply(reply: Reply) = postLocalDataSource.insertReply(reply)

    override suspend fun insertReplies(replies: List<Reply>) = postLocalDataSource.insertReplies(replies)

    override suspend fun updatePost(post: Post) = postLocalDataSource.updatePost(post)

    override suspend fun updatePosts(posts: List<Post>) = postLocalDataSource.updatePosts(posts)

    override suspend fun updateReply(reply: Reply) = postLocalDataSource.updateReply(reply)

    override suspend fun updateReplies(replies: List<Reply>) = postLocalDataSource.updateReplies(replies)

    override suspend fun deletePost(post: Post) = postLocalDataSource.deletePost(post)

    override suspend fun deletePosts(posts: List<Post>) = postLocalDataSource.deletePosts(posts)

    override suspend fun deleteReply(reply: Reply) = postLocalDataSource.deleteReply(reply)

    override suspend fun deleteReplies(replies: List<Reply>) = postLocalDataSource.deleteReplies(replies)

    override suspend fun deleteAllPosts() = postLocalDataSource.deleteAllPosts()

    override suspend fun deleteAllReplies() = postLocalDataSource.deleteAllReplies()

    override fun getAllPosts(): Flow<List<Post>> = postLocalDataSource.getAllPosts()

    override fun getPostById(id: Long): Flow<Post> = postLocalDataSource.getPostById(id)

    override fun getAllReplies(): Flow<List<Reply>> = postLocalDataSource.getAllReplies()

    override fun getRepliesByPostId(postId: Long): Flow<List<Reply>> = postLocalDataSource.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<Reply> = postLocalDataSource.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>> = postLocalDataSource.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>> = postLocalDataSource.getTopLevelRepliesByPostId(postId)

}