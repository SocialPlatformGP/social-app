package com.gp.socialapp.model


data class NetworkPost (
    val replyCount: Int=0,
    val userName:String="",
    val userPfp:String="",
    val authorEmail: String = "",
    val publishedAt: String = "",
    val title: String = "",
    val body: String = "",
    val votes: Int=0,
    val downvoted: List<String> = emptyList(),
    val upvoted: List<String> = emptyList(),
    val moderationStatus: String = "submitted",
    val editStatus: Boolean = false,
    val tags: List<Tag> = emptyList(),
    val type: String = "all",
)