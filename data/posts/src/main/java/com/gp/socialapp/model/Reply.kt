package com.gp.socialapp.model

data class Reply(
    val id : String="",
    val postId: String,
    val parentReplyId: String?,
    val content: String,
    val votes: Int=0,
    val depth: Int ,
    val createdAt: String? ,
    val deleted: Boolean=false,
    val upvoted: List<String> = emptyList(),
    val downvoted: List<String> = emptyList(),
    val collapsed: Boolean = false
)
