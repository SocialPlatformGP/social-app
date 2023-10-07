package com.gp.socialapp.repository

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow

interface ReplyRepository {
    suspend fun insertReply(replyEntity: ReplyEntity): Long
    suspend fun insertReplies(replies: List<ReplyEntity>)
    suspend fun updateReply(replyEntity: ReplyEntity)
    suspend fun updateReplies(replies: List<ReplyEntity>)
    suspend fun deleteReply(replyEntity: ReplyEntity)
    suspend fun deleteReplies(replies: List<ReplyEntity>)
    suspend fun deleteAllReplies()
    fun getAllReplies(): Flow<List<ReplyEntity>>
    fun getRepliesByPostId(postId: String): Flow<List<ReplyEntity>>
    fun getReplyById(id: Long): Flow<ReplyEntity>
    fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<ReplyEntity>>
    fun getTopLevelRepliesByPostId(postId: String): Flow<List<ReplyEntity>>


    fun getAllPostswithReplies(): Flow<List<PostWithReplies>>


}