package com.gp.chat.repository

import com.gp.chat.model.Message
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.RecentChat
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun sendMessage(chatId: String, message: NetworkMessage):String
    fun getChatMessages(chatId: String): Flow<State<List<Message>>>
     fun checkIfNewChat(userEmail:String,receiverEmail: String): Flow<State<String>>
   suspend fun updateRecent(chatId: String, message: String, userEmail: String,receiverEmail: String)
    fun getRecentChats(userEmail: String): Flow<State<List<RecentChat>>>
    suspend fun createNewChat(userEmail: String,receiverEmail: String,chatId: String)

}