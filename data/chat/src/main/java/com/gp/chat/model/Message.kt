package com.gp.chat.model

data class Message(
    val id: String?= null,
    val text: String?= null,
    val senderId: String?= null,
    val receiverId: String?= null,
    val timestamp: String?= null
)