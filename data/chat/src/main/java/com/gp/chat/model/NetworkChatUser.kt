package com.gp.chat.model

data class NetworkChatUser(
    val name : String?= null,
    val groups :Map<String,Boolean>?= null,
)
