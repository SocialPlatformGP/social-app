package com.gp.socialapp.model

data class NetworkPost (
    val authorID: Long,
    val publishedAt: String,
    val title: String,
    val body: String,
    val upvotes: Int,
    val downvotes: Int,
    val moderationStatus: String,
    val editStatus: Boolean){
    constructor() : this(0L,"","","",0,0,"",false)
}