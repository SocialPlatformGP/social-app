package com.gp.chat.model

data class NetworkChatGroup(
    val name : String?= null,
    val members :Map<String,Boolean>?= null,
)
