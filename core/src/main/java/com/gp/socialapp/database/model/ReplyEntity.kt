package com.gp.socialapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "replies")
data class ReplyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: String, // Foreign key to associate replies with their parent post
    val parentReplyId: Long?, // Foreign key to associate replies with their parent reply (can be null for top-level replies)
    val content: String,
    val upvotes: Int,
    val downvotes: Int,
    val depth: Int ,// Used to determine the indentation level of the reply in the UI
    val path:String // Used to determine the path of the reply in the UI
)

