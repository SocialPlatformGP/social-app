package com.gp.socialapp.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Post(
    val id: String="",
    val authorEmail: String,
    val publishedAt: String,
    val title: String,
    val body: String,
    val votes: Int=0,
    val downvoted: List<String> = emptyList(),
    val upvoted: List<String> = emptyList(),
    val moderationStatus: String = "submitted",
    val editStatus: Boolean = false,
):Serializable
