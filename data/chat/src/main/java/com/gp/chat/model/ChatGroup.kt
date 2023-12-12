package com.gp.chat.model

data class ChatGroup(
    var id: String="",
    var name : String= "",
    var picURL: String="",
    val members :Map<String,Boolean> = emptyMap(),
)
