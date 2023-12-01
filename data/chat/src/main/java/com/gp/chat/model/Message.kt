package com.gp.chat.model

data class Message(
    val id: String ="" ,
    val groupId : String= "",
    val message: String= "",
    val messageDate: String = "",
    val senderId: String= "",
    val senderName: String= "",
    val senderPfpURL: String = "",
    val timestamp: String= ""
)