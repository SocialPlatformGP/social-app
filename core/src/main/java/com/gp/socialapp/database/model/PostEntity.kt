package com.gp.socialapp.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id") val id: Long = 0,
    @ColumnInfo(name="author_id") val authorID: Long,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="body") val body: String,
    @ColumnInfo(name="upvotes") val upvotes: Int,
    @ColumnInfo(name="downvotes") val downvotes: Int,
    @ColumnInfo(name="moderation_status") val moderationStatus: String
)

