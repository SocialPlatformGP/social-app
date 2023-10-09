package com.gp.socialapp.source.local

import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReplyLocalDataSourceImpl @Inject constructor(
    private val replyDao: ReplyDao
) : ReplyLocalDataSource {

    override suspend fun insertReply(replyEntity: ReplyEntity): Long = replyDao.insertReply(replyEntity)

    override suspend fun insertReplies(replies: List<ReplyEntity>) = replyDao.insertReplies(replies)

    override suspend fun updateReply(replyEntity: ReplyEntity) = replyDao.updateReply(replyEntity)

    override suspend fun updateReplies(replies: List<ReplyEntity>) = replyDao.updateReplies(replies)

    override suspend fun deleteReply(replyEntity: ReplyEntity) = replyDao.deleteReply(replyEntity)

    override suspend fun deleteReplies(replies: List<ReplyEntity>) = replyDao.deleteReplies(replies)

    override suspend fun deleteAllReplies() = replyDao.deleteAllReplies()


    override fun getAllReplies(): Flow<List<ReplyEntity>> = replyDao.getAllReplies()

    override fun getRepliesByPostId(postId: String): Flow<List<ReplyEntity>> = replyDao.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<ReplyEntity> = replyDao.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<ReplyEntity>> = replyDao.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: String): Flow<List<ReplyEntity>> = replyDao.getTopLevelRepliesByPostId(postId)

}