package com.gp.socialapp.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name="id") val id: String,
    @ColumnInfo(name="author_id") val authorID: Long,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="body") val body: String,
    @ColumnInfo(name="upvotes") val upvotes: Int,
    @ColumnInfo(name="downvotes") val downvotes: Int,
    @ColumnInfo(name="moderation_status") val moderationStatus: String,
    @ColumnInfo(name="edit_status") val editStatus: Boolean
)

