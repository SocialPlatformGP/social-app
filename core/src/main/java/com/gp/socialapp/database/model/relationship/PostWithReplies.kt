package com.gp.socialapp.database.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.gp.socialapp.database.model.Post
import com.gp.socialapp.database.model.Reply

data class PostWithReplies(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val replies: List<Reply>
)

