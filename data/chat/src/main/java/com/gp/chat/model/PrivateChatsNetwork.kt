package com.gp.chat.model

data class PrivateChatsNetwork(
    val currentUser : String?= null,
    val reciverUsers :Map<String,String>?= null,
)
