package com.gp.socialapp.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    @ColumnInfo(name="id") val id: String,
    @ColumnInfo(name="author_email") val authorEmail: String,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name="title") val title: String,
    @ColumnInfo(name="body") val body: String,
    @ColumnInfo(name="votes") val votes: Int,
    @ColumnInfo(name="upvoted") var upvoted: String,
    @ColumnInfo(name="downvoted") val downvoted: String,
    @ColumnInfo(name="moderation_status") val moderationStatus: String,
    @ColumnInfo(name="edit_status") val editStatus: Boolean
)
