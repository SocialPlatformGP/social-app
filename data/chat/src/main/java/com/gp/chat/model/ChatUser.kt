package com.gp.chat.model

data class ChatUser(
    val id : String="",
    val name : String="",
    val groups :Map<String,Boolean> = emptyMap(),
)
