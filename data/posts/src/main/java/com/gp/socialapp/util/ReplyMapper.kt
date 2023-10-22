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
        return ReplyEntity(id,postId,parentReplyId,content,votes,depth,deleted,createdAt,upvotes,downvotes)
    }
    fun ReplyEntity.toNetworkModel(): NetworkReply {
        val upvotes= upvoted.split(",")
        val downvotes= downvoted.split(",")
        return NetworkReply(postId,parentReplyId,content,votes,depth,createdAt,isDeleted,upvotes,downvotes)
    }
    fun ReplyEntity.toModel(): Reply {
        val upvotes= upvoted.split(",")
        val downvotes= downvoted.split(",")
        return Reply(id,postId,parentReplyId,content,votes,depth,createdAt,isDeleted,upvotes,downvotes)
    }
    fun NetworkReply.toModel(id: String): Reply {
        return Reply(id,postId,parentReplyId,content,votes,depth,createdAt,deleted,upvoted,downvoted)
    }

    fun Reply.toEntity(): ReplyEntity {
        val upvotes= upvoted.joinToString(separator = ",")
        val downvotes= downvoted.joinToString(separator = ",")
        return ReplyEntity(id,postId,parentReplyId,content,votes,depth,deleted,createdAt,upvotes,downvotes)
    }
    fun Reply.toNetworkModel(): NetworkReply {
        return NetworkReply(postId,parentReplyId,content,votes,depth,createdAt,deleted,upvoted,downvoted)
    }

    fun Flow<List<ReplyEntity>>.toReplyFlow(): Flow<List<Reply>> {
        return map { list ->
            list.map { it.toModel()}
        }
    }



}