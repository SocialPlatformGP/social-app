package com.gp.socialapp.model

data class Reply(
    val postId: String,
    val parentReplyId: Long?,
    val content: String,
    val upvotes: Int,
    val downvotes: Int,
    val depth: Int,
    val path:List<Long>
)
