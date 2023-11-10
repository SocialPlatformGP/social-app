package com.gp.socialapp.source.local


import androidx.room.Query
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyLocalDataSource {
    suspend fun insertReply(replyEntity:Reply ): Long
    suspend fun insertReplies(replies: List<Reply>)
    suspend fun updateReply(replyEntity: Reply)
    suspend fun updateReplies(replies: List<Reply>)
    suspend fun deleteReply(replyEntity: Reply)
    suspend fun deleteReplies(replies: List<Reply>)
     fun deleteAllReplies()
    fun getAllReplies(): Flow<List<ReplyEntity>>
    fun getRepliesByPostId(postId: String): Flow<List<ReplyEntity>>
    fun getReplyById(id: Long): Flow<ReplyEntity>
    fun getRepliesByParentReplyId(parentReplyId: Long): Flow<List<ReplyEntity>>
    fun getTopLevelRepliesByPostId(postId: String): Flow<List<ReplyEntity>>

   suspend fun upVoteLocal(id:String)

  suspend  fun downVoteLocal(id:String)




}