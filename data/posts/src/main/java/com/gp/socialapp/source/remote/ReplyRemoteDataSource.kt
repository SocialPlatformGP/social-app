package com.gp.socialapp.source.remote

import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyRemoteDataSource {
    suspend fun createReply(reply: Reply)
    fun fetchReplies(postId: String): Flow<List<Reply>>
    suspend fun updateReply(reply: Reply)
    suspend fun deleteReply(reply: Reply)
}