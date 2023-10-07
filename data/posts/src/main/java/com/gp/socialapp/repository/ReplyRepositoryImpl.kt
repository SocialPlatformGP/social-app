package com.gp.socialapp.repository

import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import com.gp.socialapp.model.Reply
import com.gp.socialapp.source.remote.ReplyRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReplyRepositoryImpl @Inject constructor(
    private val replyLocalDataSource: ReplyLocalDataSource,
    private val replyRemoteDataSource: ReplyRemoteDataSource
) : ReplyRepository {

    override suspend fun insertReply(replyEntity: ReplyEntity): Long = replyLocalDataSource.insertReply(replyEntity)

    override suspend fun insertReplies(replies: List<ReplyEntity>) = replyLocalDataSource.insertReplies(replies)


    override suspend fun updateReply(replyEntity: ReplyEntity) = replyLocalDataSource.updateReply(replyEntity)


    override suspend fun updateReplies(replies: List<ReplyEntity>) = replyLocalDataSource.updateReplies(replies)


    override suspend fun deleteReply(replyEntity: ReplyEntity) = replyLocalDataSource.deleteReply(replyEntity)


    override suspend fun deleteReplies(replies: List<ReplyEntity>) = replyLocalDataSource.deleteReplies(replies)

    override suspend fun deleteAllReplies() = replyLocalDataSource.deleteAllReplies()


    override fun getAllReplies(): Flow<List<ReplyEntity>> = replyLocalDataSource.getAllReplies()

    override fun getRepliesByPostId(postId: String): Flow<List<ReplyEntity>> = replyLocalDataSource.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<ReplyEntity> = replyLocalDataSource.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<ReplyEntity>> = replyLocalDataSource.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: String): Flow<List<ReplyEntity>> = replyLocalDataSource.getTopLevelRepliesByPostId(postId)
    override fun getAllPostswithReplies(): Flow<List<PostWithReplies>> = replyLocalDataSource.getAllPostswithReplies()

    ////////////remote////////////
    override suspend fun createReply(reply: Reply) = replyRemoteDataSource.createReply(reply)
    override fun fetchReplies(postId: String): Flow<List<Reply>> = replyRemoteDataSource.fetchReplies(postId)
    override suspend fun updateReply(reply: Reply) = replyRemoteDataSource.updateReply(reply)
    override suspend fun deleteReply(reply: Reply) = replyRemoteDataSource.deleteReply(reply)
}