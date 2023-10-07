package com.gp.socialapp.source.local

import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.model.Reply
import com.gp.socialapp.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReplyLocalDataSourceImpl @Inject constructor(
    private val replyDao: ReplyDao
) : ReplyLocalDataSource {

    override suspend fun insertReply(reply: Reply): Long = replyDao.insertReply(reply)

    override suspend fun insertReplies(replies: List<Reply>) = replyDao.insertReplies(replies)

    override suspend fun updateReply(reply: Reply) = replyDao.updateReply(reply)

    override suspend fun updateReplies(replies: List<Reply>) = replyDao.updateReplies(replies)

    override suspend fun deleteReply(reply: Reply) = replyDao.deleteReply(reply)

    override suspend fun deleteReplies(replies: List<Reply>) = replyDao.deleteReplies(replies)

    override suspend fun deleteAllReplies() = replyDao.deleteAllReplies()


    override fun getAllReplies(): Flow<List<Reply>> = replyDao.getAllReplies()

    override fun getRepliesByPostId(postId: Long): Flow<List<Reply>> = replyDao.getRepliesByPostId(postId)

    override fun getReplyById(id: Long): Flow<Reply> = replyDao.getReplyById(id)

    override fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>> = replyDao.getRepliesByParentReplyId(parentReplyId)

    override fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>> = replyDao.getTopLevelRepliesByPostId(postId)
    override fun getAllPostswithReplies(): Flow<List<PostWithReplies>> = replyDao.getAllPostswithReplies()

}