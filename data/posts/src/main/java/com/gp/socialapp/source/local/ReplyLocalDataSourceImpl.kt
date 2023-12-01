package com.gp.socialapp.source.local

import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import com.gp.socialapp.model.Reply
import com.gp.socialapp.util.ReplyMapper.toEntity
import com.gp.socialapp.util.ReplyMapper.toNetworkModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReplyLocalDataSourceImpl @Inject constructor(
    private val replyDao: ReplyDao
) : ReplyLocalDataSource {

    override suspend fun insertReply(reply: Reply): Long = replyDao.insertReply(reply.toEntity())

    override suspend fun insertReplies(replies: List<Reply>) = replyDao.insertReplies(replies.map { it.toEntity() })

    override suspend fun updateReply(reply: Reply) = replyDao.updateReply(reply.toEntity())

    override suspend fun updateReplies(replies: List<Reply>) = replyDao.updateReplies(replies.map { it.toEntity() })

    override suspend fun deleteReply(reply: Reply) = replyDao.deleteReply(reply.toEntity())

    override suspend fun deleteReplies(replies: List<Reply>) = replyDao.deleteReplies(replies.map { it.toEntity() })

    override  fun deleteAllReplies() = replyDao.deleteAllReplies()


    override fun getAllReplies(): Flow<List<ReplyEntity>> = replyDao.getAllReplies()

    override fun getRepliesByPostId(postId: String): Flow<List<ReplyEntity>> = replyDao.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<ReplyEntity> = replyDao.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<ReplyEntity>> = replyDao.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: String): Flow<List<ReplyEntity>> = replyDao.getTopLevelRepliesByPostId(postId)
    override suspend fun upVoteLocal(id: String) = replyDao.upVoteLocal(id)

    override suspend fun downVoteLocal(id: String) = replyDao.downVoteLocal(id)


}