package com.gp.core.repository

import com.gp.core.source.local.ReplyLocalDataSource
import com.gp.core.database.model.Reply
import com.gp.core.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReplyRepositoryImpl @Inject constructor(
    private val replyLocalDataSource: ReplyLocalDataSource
) : ReplyRepository {

    override suspend fun insertReply(reply: Reply): Long = replyLocalDataSource.insertReply(reply)

    override suspend fun insertReplies(replies: List<Reply>) = replyLocalDataSource.insertReplies(replies)


    override suspend fun updateReply(reply: Reply) = replyLocalDataSource.updateReply(reply)

    override suspend fun updateReplies(replies: List<Reply>) = replyLocalDataSource.updateReplies(replies)


    override suspend fun deleteReply(reply: Reply) = replyLocalDataSource.deleteReply(reply)

    override suspend fun deleteReplies(replies: List<Reply>) = replyLocalDataSource.deleteReplies(replies)

    override suspend fun deleteAllReplies() = replyLocalDataSource.deleteAllReplies()


    override fun getAllReplies(): Flow<List<Reply>> = replyLocalDataSource.getAllReplies()

    override fun getRepliesByPostId(postId: Long): Flow<List<Reply>> = replyLocalDataSource.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<Reply> = replyLocalDataSource.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>> = replyLocalDataSource.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>> = replyLocalDataSource.getTopLevelRepliesByPostId(postId)
    override fun getPostwithMappedReplies(postId: Long): Flow<PostWithReplies> = replyLocalDataSource.getPostwithReplies(postId)
    override fun getAllPostswithReplies(): Flow<List<PostWithReplies>> = replyLocalDataSource.getAllPostswithReplies()


}