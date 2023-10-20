package com.gp.socialapp.repository

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.source.remote.ReplyRemoteDataSource
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
    override fun getReplies(postId: String): Flow<List<ReplyEntity>> {
        return if (networkStatus.isOnline()) {
            val replies = replyRemoteDataSource.fetchReplies(postId)
            repositoryScope.launch {
                replies.collect {
                    replyLocalDataSource.insertReplies(it)
                }
            }
            replies
        } else {
            replyLocalDataSource.getRepliesByPostId(postId)
        }
    }

    override suspend fun insertReply(replyEntity: NetworkReply) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.createReply(replyEntity)
        }
        //todo return state errror  in else
    }

    override suspend fun insertReplies(replies: List<ReplyEntity>) =
        replyLocalDataSource.insertReplies(replies)

    override suspend fun updateReply(replyEntity: ReplyEntity) =
        replyLocalDataSource.updateReply(replyEntity)


    ////////////remote////////////
    override suspend fun upVoteReply(reply: ReplyEntity) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.upVoteReply(reply)
            replyLocalDataSource.upVoteLocal(reply.id)
        } else {
            //todo return state errror  in else
        }

    }

    override suspend fun downVoteReply(reply: ReplyEntity) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.downVoteReply(reply)
            replyLocalDataSource.downVoteLocal(reply.id)
        } else {
            //todo return state errror  in else
        }
    }

    override suspend fun deleteReply(reply: ReplyEntity) {
        if (networkStatus.isOnline()) {
            replyRemoteDataSource.updateReplyRemote(reply)
            replyLocalDataSource.updateReply(reply)
        } else {
            //todo return state errror  in else
        }
    }
}