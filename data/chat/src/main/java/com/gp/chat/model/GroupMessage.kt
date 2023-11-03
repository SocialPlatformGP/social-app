package com.gp.chat.model

import java.util.Date

data class GroupMessage (
    val id: String?= null,
    val message: String?= null,
    val messageDate: String? = null,
    val senderId: String?= null,
    val senderName: String?= null,
    val senderPfpURL: String? = null,
    val timestamp: String?= null
)
