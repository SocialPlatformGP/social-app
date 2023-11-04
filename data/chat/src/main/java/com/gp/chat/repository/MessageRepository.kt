package com.gp.chat.repository


import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun sendMessage(chatId: String, message: Message)
    fun getChatMessages(chatId: String): Flow<List<Message>>
    fun fetchGroupChatMessages(groupId: String): Flow<List<Message>>
    fun sendGroupMessage(message: Message, recentChat: RecentChat): Flow<State<Nothing>>
}