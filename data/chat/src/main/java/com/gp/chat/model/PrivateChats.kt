package com.gp.chat.model

data class PrivateChats(
    val currentUser: String? = null,
    val reciverUsers: Map<String, String> = emptyMap(),
)
