package com.gp.chat.model

data class NetworkChatGroup(
    val name : String="",
    val picURL: String="",
    val members :Map<String,Boolean> = emptyMap(),
)
