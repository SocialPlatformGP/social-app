package com.gp.socialapp.source.remote

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow

interface ReplyRemoteDataSource {
    suspend fun createReply(reply: Reply)
    fun fetchReplies(postId: String): Flow<List<Reply>>
    suspend fun updateReplyRemote(reply: Reply)
    suspend fun deleteReply(reply: Reply)

    suspend fun upVoteReply(reply: Reply)
    suspend fun downVoteReply(reply: Reply)
    fun getReplyCountByPostId(postId: String): Flow<Int>
}