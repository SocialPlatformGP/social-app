package com.gp.chat.model

data class NetworkMessage(
    val message: String= "",
    val messageDate: String = "",
    val senderId: String= "",
    val senderName: String= "",
    val senderPfpURL: String = "",
    val timestamp: String= ""
)