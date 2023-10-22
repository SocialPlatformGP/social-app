package com.gp.socialapp.model

data class NetworkReply(
    val postId: String,
    val parentReplyId: String?,
    val content: String,
    val votes: Int,
    val depth: Int ,
    val createdAt: String? ,
    val deleted: Boolean,
    val upvoted: List<String>,
    val downvoted: List<String>

){
    constructor() : this("",null,"",0,0,"",false, emptyList(), emptyList())
}
