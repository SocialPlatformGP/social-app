package com.gp.chat.listener

import android.view.View
import com.gp.chat.model.Message

interface OnMessageClickListener {
    fun deleteMessage(messageId:String,chatId:String)
    fun updateMessage(messageId:String,chatId:String,messageBody:String)
}