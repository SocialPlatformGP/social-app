package com.gp.chat.repository

import com.gp.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun sendMessage(chatId: String, message: Message)
    fun getChatMessages(chatId: String): Flow<List<Message>>
}