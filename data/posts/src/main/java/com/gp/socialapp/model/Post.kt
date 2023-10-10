package com.gp.socialapp.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Post(

    val id: String,
    val authorID: Long,
    val publishedAt: String,
    val title: String,
    val body: String,
    val upvotes: Int,
    val downvotes: Int,
    val moderationStatus: String
)
