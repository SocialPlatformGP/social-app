package com.gp.chat.model

data class ChatGroup(
    val id: String?= null,
    val name : String?= null,
    val members :Map<String,Boolean>?= null,
)
