package com.gp.socialapp.source.remote

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyRemoteDataSource {
    suspend fun createReply(reply: NetworkReply)
    fun fetchReplies(postId: String): Flow<List<ReplyEntity>>
    suspend fun updateReplyRemote(reply: ReplyEntity)
    suspend fun deleteReply(reply: Reply)
}