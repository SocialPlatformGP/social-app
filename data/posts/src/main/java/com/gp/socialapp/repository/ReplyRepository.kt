package com.gp.socialapp.repository

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.database.model.relationship.PostWithReplies
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyRepository {
    fun getReplies(postId: String): Flow<List<Reply>>
    suspend fun insertReplies(replies: List<Reply>)
    suspend fun updateReply(replyEntity: Reply)
    suspend fun deleteReply(replyEntity: Reply)

    ////////////remote////////////

    suspend fun upVoteReply(reply: Reply)
    suspend fun downVoteReply(reply: Reply)
    suspend fun insertReply(reply: Reply)

}