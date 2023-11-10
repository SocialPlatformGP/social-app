package com.gp.chat.model

data class NetworkRecentChat(
    val lastMessage: String= "",
    val timestamp: String= "",
    val title : String= "",
    val senderName: String= "",
    val receiverName: String= "",
    val isPrivateChat : Boolean= false,
    val picUrl : String= "",
)
