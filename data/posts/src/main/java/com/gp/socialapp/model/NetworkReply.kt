package com.gp.socialapp.model

data class NetworkReply(
    val postId: String,
    val parentReplyId: String?,
    val content: String,
    val upvotes: Int,
    val downvotes: Int,
    val depth: Int ,
    val isDeleted: Boolean
){
    constructor() : this("",null,"",0,0,0,false)
}
