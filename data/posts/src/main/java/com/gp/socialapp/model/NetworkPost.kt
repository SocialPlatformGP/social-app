package com.gp.socialapp.model

data class NetworkPost (
    val autherEmail: String,
    val publishedAt: String,
    val title: String,
    val body: String,
    val votes: Int,
    val downvoted: List<String>,
    val upvoted: List<String>,
    val moderationStatus: String,
    val editStatus: Boolean
){
    constructor() : this("","","","",0, emptyList(), emptyList(),"",false)
}