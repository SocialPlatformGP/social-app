package com.gp.socialapp.util

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost

object PostMapper {
    fun NetworkPost.toEntity(id: String): PostEntity{
        return PostEntity(id, authorID, publishedAt, title, body, upvotes, downvotes, moderationStatus)
    }
    fun PostEntity.toNetworkModel(): NetworkPost{
        return NetworkPost(authorID, publishedAt, title, body, upvotes, downvotes, moderationStatus)
    }




}