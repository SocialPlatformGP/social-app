package com.gp.chat.model

data class NetworkChatUser(
    val name : String="",
    val groups :Map<String,Boolean> = emptyMap(),
)
