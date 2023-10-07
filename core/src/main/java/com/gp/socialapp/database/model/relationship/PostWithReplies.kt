package com.gp.socialapp.database.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity

data class PostWithReplies(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val replies: List<ReplyEntity>
)

