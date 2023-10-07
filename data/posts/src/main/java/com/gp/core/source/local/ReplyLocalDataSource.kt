package com.gp.core.source.local


import com.gp.core.database.model.Reply
import com.gp.core.database.model.relationship.PostWithReplies
import kotlinx.coroutines.flow.Flow

interface ReplyLocalDataSource {
    suspend fun insertReply(reply: Reply): Long
    suspend fun insertReplies(replies: List<Reply>)
    suspend fun updateReply(reply: Reply)
    suspend fun updateReplies(replies: List<Reply>)
    suspend fun deleteReply(reply: Reply)
    suspend fun deleteReplies(replies: List<Reply>)
    suspend fun deleteAllReplies()
    fun getAllReplies(): Flow<List<Reply>>
    fun getRepliesByPostId(postId: Long): Flow<List<Reply>>
    fun getReplyById(id: Long): Flow<Reply>
    fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<Reply>>
    fun getTopLevelRepliesByPostId(postId: Long): Flow<List<Reply>>

    fun getPostwithReplies(postId: Long): Flow<PostWithReplies>

    fun getAllPostswithReplies(): Flow<List<PostWithReplies>>


}