package com.gp.chat.model

data class RecentChat(
    val id: String?= null,
    val lastMessage: String?= null,
    val timestamp: String?= null,
    val title : String?= null,
    val senderName: String?= null,
    val receiverName: String?= null,
    val isPrivateChat : Boolean?= null,
    val picUrl : String?= null,
)
