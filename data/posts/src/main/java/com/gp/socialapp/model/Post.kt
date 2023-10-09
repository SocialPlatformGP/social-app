package com.gp.socialapp.model

import com.google.type.DateTime
import java.time.LocalDateTime

data class Post(
    val authorName: String,
    val publishedAt: LocalDateTime,
    val title: String,
    val body: String,
    val upvotes: Int,
    val downvotes: Int,
    val editStatus: Boolean
)
