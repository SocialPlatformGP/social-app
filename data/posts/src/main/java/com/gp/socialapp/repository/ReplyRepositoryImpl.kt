package com.gp.socialapp.repository

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.source.remote.ReplyRemoteDataSource
import com.gp.socialapp.util.ReplyMapper.toReplyFlow
import com.gp.socialapp.utils.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReplyRepositoryImpl @Inject constructor(
    private val replyLocalDataSource: ReplyLocalDataSource,
    private val replyRemoteDataSource: ReplyRemoteDataSource,
    private val networkStatus: NetworkStatus,
    private val repositoryScope: CoroutineScope

) : ReplyRepository {
    override fun getReplies(postId: String): Flow<List<Reply>> {
        return if (networkStatus.isOnline()) {
            val replies = replyRemoteDataSource.fetchReplies(postId)
            repositoryScope.launch {
                replies.collect {
                    replyLocalDataSource.insertReplies(it)
                }
            }
            replies
        } else {
            replyLocalDataSource.getRepliesByPostId(postId).toReplyFlow()
        }
    }

    override suspend fun insertReply(reply: Reply) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.createReply(reply)
        }
        //todo return state errror  in else
    }

    override suspend fun insertReplies(replies: List<Reply>) =
        replyLocalDataSource.insertReplies(replies)

    override suspend fun updateReply(reply: Reply) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.updateReplyRemote(reply)
            replyLocalDataSource.updateReply(reply)
        }
        //todo return state errror  in else

    }


    ////////////remote////////////
    override suspend fun upVoteReply(reply: Reply) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.upVoteReply(reply)
            replyLocalDataSource.upVoteLocal(reply.id)
        } else {
            //todo return state errror  in else
        }

    }

    override suspend fun downVoteReply(reply: Reply) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.downVoteReply(reply)
            replyLocalDataSource.downVoteLocal(reply.id)
        } else {
            //todo return state errror  in else
        }
    }

    override suspend fun deleteReply(reply: Reply) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.updateReplyRemote(reply)
            replyLocalDataSource.updateReply(reply)
        } else {
            //todo return state errror  in else
        }
    }
}