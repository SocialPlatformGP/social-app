package com.gp.socialapp.util

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post

object PostMapper {
    fun NetworkPost.toEntity(id: String): PostEntity{
        return PostEntity(id, authorID, publishedAt, title, body, upvotes, downvotes, moderationStatus, editStatus)
    }
    fun PostEntity.toNetworkModel(): NetworkPost{
        return NetworkPost(authorID, publishedAt, title, body, upvotes, downvotes, moderationStatus, editStatus)
    }
    fun Post.toNetworkModel(authorId: Long): NetworkPost{
        return NetworkPost(authorId, publishedAt.toString(), title, body, upvotes, downvotes,"", editStatus)
    }
}