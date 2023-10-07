package com.gp.socialapp.models.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.gp.socialapp.models.Post
import com.gp.socialapp.models.Reply

data class PostWithReplies(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val replies: List<Reply>
)

