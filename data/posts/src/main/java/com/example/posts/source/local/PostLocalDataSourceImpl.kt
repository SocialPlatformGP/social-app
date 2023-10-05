package com.example.posts.source.local

import com.gp.socialapp.dao.PostDao
import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply
import com.gp.socialapp.models.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostLocalDataSourceImpl @Inject constructor(
    private val postDao: PostDao
) : PostLocalDataSource{
    override suspend fun insertPost(post: Post) :Long = postDao.insertPost(post)

    override suspend fun insertPosts(posts: List<Post>) = postDao.insertPosts(posts)

    override suspend fun insertReply(reply: Reply): Long = postDao.insertReply(reply)

    override suspend fun insertReplies(replies: List<Reply>) = postDao.insertReplies(replies)

    override suspend fun updatePost(post: Post) = postDao.updatePost(post)

    override suspend fun updatePosts(posts: List<Post>) = postDao.updatePosts(posts)

    override suspend fun updateReply(reply: Reply) = postDao.updateReply(reply)

    override suspend fun updateReplies(replies: List<Reply>) = postDao.updateReplies(replies)

    override suspend fun deletePost(post: Post) = postDao.deletePost(post)

    override suspend fun deletePosts(posts: List<Post>) = postDao.deletePosts(posts)

    override suspend fun deleteReply(reply: Reply) = postDao.deleteReply(reply)

    override suspend fun deleteReplies(replies: List<Reply>) = postDao.deleteReplies(replies)

    override suspend fun deleteAllPosts() = postDao.deleteAllPosts()

    override suspend fun deleteAllReplies() = postDao.deleteAllReplies()

    override fun getAllPosts(): Flow<List<Post>> = postDao.getAllPosts()

    override fun getPostById(id: Long): Flow<Post> = postDao.getPostById(id)

    override fun getAllReplies(): Flow<List<Reply>> = postDao.getAllReplies()

    override fun getRepliesByPostId(postId: Long): Flow<List<Reply>> = postDao.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<Reply> = postDao.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>> = postDao.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>> = postDao.getTopLevelRepliesByPostId(postId)
    override fun getPostwithReplies(postId: Long): Flow<PostWithReplies> = postDao.getPostwithReplies(postId)
    override fun getAllPostswithReplies(): Flow<List<PostWithReplies>> = postDao.getAllPostswithReplies()

}