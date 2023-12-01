package com.gp.chat.util

import com.gp.chat.model.ChatGroup
import com.gp.chat.model.ChatUser
import com.gp.chat.model.Message
import com.gp.chat.model.NetworkChatGroup
import com.gp.chat.model.NetworkChatUser
import com.gp.chat.model.NetworkMessage
import com.gp.chat.model.NetworkRecentChat
import com.gp.chat.model.PrivateChats
import com.gp.chat.model.PrivateChatsNetwork
import com.gp.chat.model.RecentChat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object ChatMapper {

    fun ChatGroup.toNetworkChatGroup() = NetworkChatGroup(
        name = name,
        members = members
    )

    fun NetworkChatGroup.toChatGroup(id: String) = ChatGroup(
        id = id,
        name = name,
        members = members
    )

    fun ChatUser.toNetworkChatUser() = NetworkChatUser(
        name = name,
        groups = groups
    )

    fun NetworkChatUser.toChatUser(id: String) = ChatUser(
        id = id,
        name = name,
        groups = groups
    )

    fun Message.toNetworkMessage() = NetworkMessage(
        senderId = senderId,
        message = message,
        timestamp = timestamp,
        messageDate = messageDate,
        senderName = senderName,
        senderPfpURL = senderPfpURL
    )

    fun NetworkMessage.toModel(id: String,groupId:String) = Message(
        id = id,
        groupId=groupId,
        senderId = senderId,
        message = message,
        timestamp = timestamp,
        messageDate = messageDate,
        senderName = senderName,
        senderPfpURL = senderPfpURL
    )



    fun RecentChat.toNetworkRecentChat() = NetworkRecentChat(
        lastMessage = lastMessage,
        timestamp = timestamp,
        title = title,
        senderName = senderName,
        receiverName = receiverName,
        privateChat = privateChat,
        picUrl = picUrl
    )

    fun NetworkRecentChat.toRecentChat(id: String) = RecentChat(
        id = id,
        lastMessage = lastMessage,
        timestamp = timestamp,
        title = title,
        senderName = senderName,
        receiverName = receiverName,
        privateChat = privateChat,
        picUrl = picUrl
    )


    fun PrivateChats.toNetworkPrivateChats() = PrivateChatsNetwork(
        reciverUsers = reciverUsers,
    )
    fun PrivateChatsNetwork.toPrivateChats(id: String) = PrivateChats(
        currentUser = id,
        reciverUsers = reciverUsers,
    )
    fun RecentChat.toMap()= mapOf(
        "lastMessage" to lastMessage,
        "timestamp" to timestamp,
        "title" to title,
        "senderName" to senderName,
        "receiverName" to receiverName,
        "privateChat" to privateChat,
        "picUrl" to picUrl
    )


}