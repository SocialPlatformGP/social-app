package com.gp.socialapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val replyCount: Int,
    val userName:String,
    val userPfp:String,
    val authorEmail: String,
    val publishedAt: String,
    val title: String,
    val body: String,
    val votes: Int,
    val downvoted: String,
    val upvoted: String,
    val moderationStatus: String,
    val editStatus: Boolean ,
    val tags: List<TagEntity>,
    val type: String,
)
