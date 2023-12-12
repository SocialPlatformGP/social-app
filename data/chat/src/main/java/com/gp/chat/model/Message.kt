package com.gp.chat.model

import android.net.Uri
import androidx.core.net.toUri

data class Message(
    val id: String ="" ,
    val groupId : String= "",
    val message: String= "",
    val messageDate: String = "",
    val senderId: String= "",
    val senderName: String= "",
    val senderPfpURL: String = "",
    val timestamp: String= "",
    val fileURI:Uri ="".toUri() ,
    val fileType: String = "text",
    val fileNames: String = "",
)