package com.gp.socialapp.util

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.TagEntity
import com.gp.socialapp.model.NetworkPost
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PostMapper {
    fun NetworkPost.toEntity(id: String): PostEntity{
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
            editStatus = editStatus,
            replyCount = replyCount,
            userName = userName,
            userPfp = userPfp,
            tags = tags.toEntity(),
            type = type,
            attachments = attachments

        )
    }
    fun PostEntity.toNetworkModel(): NetworkPost{
        return NetworkPost(
            authorEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted.split(","),
            downvoted = downvoted.split(","),
            moderationStatus = moderationStatus,
            editStatus = editStatus,
            replyCount = replyCount,
            userName = userName,
            userPfp = userPfp,
            tags = tags.toModel(),
            type = type,
            attachments = attachments
        )
    }
    fun Post.toNetworkModel(): NetworkPost{
        return NetworkPost(
            replyCount = replyCount,
            userName = userName,
            userPfp = userPfp,
            authorEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted,
            downvoted = downvoted,
            moderationStatus = moderationStatus,
            editStatus = editStatus,
            tags = tags,
            type = type,
            attachments = attachments
        )
    }
    fun NetworkPost.toModel(id: String): Post{
        return Post(
            id = id,
            replyCount = replyCount,
            userName = userName,
            userPfp = userPfp,
            authorEmail = authorEmail,
            publishedAt = publishedAt,
            title = title,
            body = body,
            votes = votes,
            upvoted = upvoted,
            downvoted = downvoted,
            moderationStatus = moderationStatus,
            editStatus = editStatus,
            tags = tags,
            type = type,
            attachments = attachments
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
            editStatus = editStatus,
            replyCount = replyCount,
            userName = userName,
            userPfp = userPfp,
            tags = tags.toEntity(),
            type = type,
            attachments = attachments
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
            editStatus = editStatus,
            tags = tags.toModel(),
            type = type,
            attachments = attachments
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
    fun Tag.toEntity(): TagEntity {
        return TagEntity(
            label = label,
            hexColor = hexColor
        )
    }
    fun TagEntity.toModel(): Tag{
        return Tag(
            label = label,
            hexColor = hexColor
        )
    }
    fun List<Tag>.toEntity(): List<TagEntity> {
        return map { it.toEntity() }
    }
    fun List<TagEntity>.toModel(): List<Tag> {
        return map { it.toModel() }
    }

}