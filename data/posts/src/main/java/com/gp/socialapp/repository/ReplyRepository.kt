package com.gp.socialapp.repository

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyRepository {
    fun getReplies(postId: String): Flow<List<ReplyEntity>>
    suspend fun insertReplies(replies: List<ReplyEntity>)
    suspend fun updateReply(replyEntity: ReplyEntity)
    suspend fun deleteReply(replyEntity: ReplyEntity)

    ////////////remote////////////

    suspend fun upVoteReply(reply: ReplyEntity)
    suspend fun downVoteReply(reply: ReplyEntity)
    suspend fun insertReply(replyEntity: NetworkReply)
}