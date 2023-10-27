package com.gp.socialapp.model

data class NetworkPost (
    val replyCount: Int=0,
    val userName:String="",
    val userPfp:String="",
    val authorEmail: String,
    val publishedAt: String,
    val title: String,
    val body: String,
    val votes: Int=0,
    val downvoted: List<String> = emptyList(),
    val upvoted: List<String> = emptyList(),
    val moderationStatus: String = "submitted",
    val editStatus: Boolean = false,
){
    constructor(): this(
        replyCount = 0,
        userName = "",
        userPfp = "",
        authorEmail = "",
        publishedAt = "",
        title = "",
        body = "",
        votes = 0,
        downvoted = emptyList(),
        upvoted = emptyList(),
        moderationStatus = "submitted",
        editStatus = false

    )
}