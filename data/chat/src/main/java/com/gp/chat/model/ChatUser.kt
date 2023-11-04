package com.gp.chat.model

data class ChatUser(
    val id : String?= null,
    val name : String?= null,
    val groups :Map<String,Boolean>?= null,
)
