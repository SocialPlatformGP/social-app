package com.gp.chat.model

data class RecentChat(
    val id: String= "",
    val lastMessage: String= "",
    val timestamp: String="" ,
    val title : String= "",
    val senderName: String= "",
    val receiverName: String= "",
    val privateChat : Boolean= false,
    val senderPicUrl : String= "",
    val receiverPicUrl : String= "",
)
