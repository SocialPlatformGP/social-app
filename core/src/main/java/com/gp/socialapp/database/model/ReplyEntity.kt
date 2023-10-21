package com.gp.socialapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "replies")
data class ReplyEntity(
    @PrimaryKey
    val id: String ,
    val postId: String, // Foreign key to associate replies with their parent post
    val parentReplyId: String?, // Foreign key to associate replies with their parent reply (can be null for top-level replies)
    val content: String,
    val upvotes: Int,
    val downvotes: Int,
    val depth: Int ,// Used to determine the indentation level of the reply in the UI
    val isDeleted: Boolean,
    val createdAt: String?,
    val collapsed: Boolean,
    val upvoted: Boolean,
    val downvoted: Boolean

)

