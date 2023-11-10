package com.gp.chat.model

data class NetworkMessage(
    val message: String?= null,
    val messageDate: String? = null,
    val senderId: String?= null,
    val senderName: String?= null,
    val senderPfpURL: String? = null,
    val timestamp: String?= null
)