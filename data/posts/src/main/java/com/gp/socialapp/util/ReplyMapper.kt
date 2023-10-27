package com.gp.socialapp.util

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.util.ReplyMapper.toModel
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
        return Reply(id,autherEmail,postId,parentReplyId,content,votes,depth,createdAt,deleted,upvoted,downvoted)
    }

    fun Reply.toEntity(): ReplyEntity {
        val upvotes= upvoted.joinToString(separator = ",")
        val downvotes= downvoted.joinToString(separator = ",")
        return ReplyEntity(id,authorEmail,postId,parentReplyId,content,votes,depth,deleted,createdAt,upvotes,downvotes,editStatus)
    }
    fun Reply.toNetworkModel(): NetworkReply {
        return NetworkReply(authorEmail,postId,parentReplyId,content,votes,depth,createdAt,deleted,upvoted,downvoted,editStatus)
    }

    fun Flow<List<ReplyEntity>>.toReplyFlow(): Flow<List<Reply>> {
        return map { list ->
            list.map { it.toModel()}
        }
    }



}