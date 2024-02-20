package com.gp.socialapp.util

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Reply
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ReplyMapper {

    fun NetworkReply.toEntity(id: String): ReplyEntity {
        val upvotes= upvoted.joinToString(separator = ",")
        val downvotes= downvoted.joinToString(separator = ",")
        return ReplyEntity(
            id,
            autherEmail,
            postId,
            parentReplyId,
            content,
            votes,
            depth,
            deleted,
            createdAt,
            upvotes,
            downvotes,
            editStatus
        )
    }
    fun ReplyEntity.toNetworkModel(): NetworkReply {
        val upvotes= upvoted.split(",")
        val downvotes= downvoted.split(",")
        return NetworkReply(
            authorEmail,
            postId,
            parentReplyId,
            content,
            votes,
            depth,
            createdAt,
            isDeleted,
            upvotes,
            downvotes,
            editStatus
        )
    }
    fun ReplyEntity.toModel(): Reply {
        val upvotes= upvoted.split(",")
        val downvotes= downvoted.split(",")
        return Reply(id,authorEmail,postId,parentReplyId,content,votes,depth,createdAt,isDeleted,upvotes,downvotes)
    }
    fun NetworkReply.toModel(id: String): Reply {
        return Reply(
            id,
            autherEmail,
            postId,
            parentReplyId,
            content,
            votes,
            depth,
            createdAt,
            deleted,
            upvoted,
            downvoted,
            userPfp = userPfp,
            authorName = authorName
        )
    }

    fun Reply.toEntity(): ReplyEntity {
        val upvotes= upvoted.joinToString(separator = ",")
        val downvotes= downvoted.joinToString(separator = ",")
        return ReplyEntity(id,authorEmail,postId,parentReplyId,content,votes,depth,deleted,createdAt,upvotes,downvotes,editStatus)
    }
    fun Reply.toNetworkModel(): NetworkReply {
        return NetworkReply(authorEmail,postId,parentReplyId,content,votes,depth,createdAt,deleted,upvoted,downvoted,editStatus, userPfp = userPfp, authorName = authorName)
    }

    fun Flow<List<ReplyEntity>>.toReplyFlow(): Flow<List<Reply>> {
        return map { list ->
            list.map { it.toModel()}
        }
    }



}