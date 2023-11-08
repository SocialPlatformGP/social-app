package com.gp.chat.source.remote

import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRemoteDataSource {
    suspend fun sendMessage(chatId: String, message: Message)
    fun getChatMessages(chatId: String): Flow<List<Message>>
    fun fetchGroupMessages(groupId: String): Flow<List<Message>>
    fun sendGroupMessage(message: Message, recentChat: RecentChat): Flow<State<Nothing>>
    fun createGroupChat(group: NetworkChatGroup, recentChat: NetworkRecentChat): Flow<State<String>>
}