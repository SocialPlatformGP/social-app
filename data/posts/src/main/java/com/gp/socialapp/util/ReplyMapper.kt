package com.gp.socialapp.util

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post

object ReplyMapper {

    fun NetworkReply.toEntity(id: String): ReplyEntity {
        return ReplyEntity(id,postId,parentReplyId,content,upvotes,downvotes,depth,isDeleted)
    }
    fun ReplyEntity.toNetworkModel(): NetworkReply {
        return NetworkReply(postId,parentReplyId,content,upvotes,downvotes,depth,isDeleted)
    }


}