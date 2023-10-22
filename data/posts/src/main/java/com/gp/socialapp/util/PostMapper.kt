package com.gp.socialapp.util

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PostMapper {
    fun NetworkPost.toEntity(id: String): PostEntity{
        return PostEntity(
            id= id,
            authorEmail = autherEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted.joinToString(separator = ","),
            downvoted = downvoted.joinToString(separator = ","),
            moderationStatus = moderationStatus,
            editStatus = editStatus
        )
    }
    fun PostEntity.toNetworkModel(): NetworkPost{
        return NetworkPost(
            autherEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted.split(","),
            downvoted = downvoted.split(","),
            moderationStatus = moderationStatus,
            editStatus = editStatus
        )
    }
    fun Post.toNetworkModel(): NetworkPost{
        return NetworkPost(
            autherEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted,
            downvoted = downvoted,
            moderationStatus = moderationStatus,
            editStatus = editStatus
        )
    }
    fun NetworkPost.toModel(id: String): Post{
        return Post(
            id = id,
            authorEmail = autherEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted,
            downvoted = downvoted,
            moderationStatus = moderationStatus,
            editStatus = editStatus
        )
    }
    fun Post.toEntity(): PostEntity{
        return PostEntity(
            id= id,
            authorEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted.joinToString(separator = ","),
            downvoted = downvoted.joinToString(separator = ","),
            moderationStatus = moderationStatus,
            editStatus = editStatus
        )
    }
    fun PostEntity.toModel(): Post{
        return Post(
            id = id,
            authorEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted.split(","),
            downvoted = downvoted.split(","),
            moderationStatus = moderationStatus,
            editStatus = editStatus
        )
    }

    fun Flow<List<PostEntity>>.toPostFlow(): Flow<List<Post>> {
        return map { list ->
            list.map { it.toModel()}
        }
    }
    fun Flow<PostEntity>.toModel(): Flow<Post> {
        return map { it.toModel()}
    }




}