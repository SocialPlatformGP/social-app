package com.gp.chat.presentation.groupchat

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.presentation.privateChat.MessageState
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.DateUtils.getTimeStamp
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val messageRepo: MessageRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _chatMessagesFlow = MutableStateFlow(emptyList<Message>())
    val chatMessagesFlow = _chatMessagesFlow.asStateFlow()
    val currentMessageState = MutableStateFlow(MessageState())
    private val currentUser = Firebase.auth.currentUser
    private var currentEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    private var ChatId = "-1"

    fun setData(chatId: String) {
        ChatId = chatId
        Log.d("zarea1", "setData: getting chat id $ChatId")
        fetchGroupChatMessages()
    }

    fun fetchGroupChatMessages() {
        viewModelScope.launch(Dispatchers.IO) {
                    Log.d("zarea1", "fetchGroupChatMessages: getting chat id $ChatId")

            messageRepo.fetchGroupChatMessages(ChatId).collect {
                _chatMessagesFlow.value = it
                Log.d("zarea1", "incoming messages: $it")
            }
        }
    }

    fun onSendMessage() {

        if (currentMessageState.value.message.isEmpty() && currentMessageState.value.fileTypes == "text") {
            currentMessageState.value = currentMessageState.value.copy(error = "message is empty")
            return
        } else {
            val message = Message(
                id = "",
                groupId = ChatId,
                message = currentMessageState.value.message,
                messageDate = SimpleDateFormat("MMMM dd, yyyy").format(Date()),
                senderId = currentEmail,
                senderName = currentUser?.displayName!!,
                senderPfpURL = currentUser.photoUrl.toString(),
                timestamp = getTimeStamp(Date()),
                fileURI = currentMessageState.value.fileUri ?: "".toUri(),
                fileType = currentMessageState.value.fileTypes ?: "", //TODO: change to file type
                fileNames = currentMessageState.value.fileName ?: ""
            )
            val recentChat = RecentChat(
                lastMessage =
                if (currentMessageState.value.fileTypes == "text") {
                    currentMessageState.value.message
                } else {
                    currentMessageState.value.fileName
                },
                timestamp = message.timestamp
            )
            viewModelScope.launch(Dispatchers.IO) {
                messageRepo.sendGroupMessage(message, recentChat).collect {
                    when (it) {
                        is State.Success -> {
                            currentMessageState.value = MessageState()
                            Log.d("seerde", "Success")
                        }

                        is State.Error -> {
                            Log.d("seerde", "Error: ${it.message}")
                        }

                        else -> {
                            Log.d("seerde", "Loading")
                        }
                    }
                }
            }
        }
    }
    fun sendFile(uri: Uri, type: String, fileName: String) {
        currentMessageState.value = currentMessageState.value.copy(fileName = fileName)
        currentMessageState.value = currentMessageState.value.copy(fileUri = uri)
        currentMessageState.value = currentMessageState.value.copy(fileTypes = type)
        onSendMessage()
    }
    fun deleteMessage(messageId: String, chatId: String) {
        messageRepo.deleteMessage(messageId, chatId)
    }

    fun updateMessage(messageId: String, chatId: String, updatedText: String) {
        messageRepo.updateMessage(messageId, chatId, updatedText)
    }


}