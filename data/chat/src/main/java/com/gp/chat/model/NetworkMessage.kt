package com.gp.chat.model

import android.net.Uri
import androidx.core.net.toUri

data class NetworkMessage(
    val message: String = "",
    val messageDate: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderPfpURL: String = "",
    val timestamp: String = "",
    val fileURI: String = "",
    val fileType: String = "text",
    val fileNames: String = "",
)