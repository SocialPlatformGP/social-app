package com.gp.chat.repository

import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun sendMessage(chatId: String, message: NetworkMessage): String
    fun getChatMessages(chatId: String): Flow<List<Message>>
}