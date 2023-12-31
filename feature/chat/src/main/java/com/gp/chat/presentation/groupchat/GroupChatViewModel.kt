package com.gp.chat.presentation.groupchat

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
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
    private var title = ""
    private var photoUrl = ""

    fun setData(chatId: String, title: String, photoUrl: String) {
        ChatId = chatId
        this.title = title
        this.photoUrl = photoUrl
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSendMessage() {
        Log.d("zarea2", "start of message send  $ChatId")
        if (currentMessageState.value.message.isEmpty() && currentMessageState.value.fileTypes == "text") {
            return
        } else {
            val currentTime: ZonedDateTime =now()
            val  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)
            val formatted = currentTime.format(formatter)

            viewModelScope.launch(Dispatchers.IO) {
                val message = Message(
                    id = "",
                    groupId = ChatId,
                    message = currentMessageState.value.message,
                    messageDate = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
                        .format(currentTime),
                    senderId = currentEmail,
                    senderName = currentUser?.displayName!!,
                    senderPfpURL = currentUser.photoUrl.toString(),
                    timestamp = formatted,
                    fileURI = currentMessageState.value.fileUri ?: "".toUri(),
                    fileType = currentMessageState.value.fileTypes ?: "",
                    fileNames = currentMessageState.value.fileName ?: ""
                )
                val recentChat = RecentChat(
                    lastMessage = if (currentMessageState.value.fileTypes == "text") {
                        "${currentUser.displayName}: ${currentMessageState.value.message}"
                    } else {
                        "${currentUser.displayName}: ${currentMessageState.value.fileName}"
                    },
                    timestamp = message.timestamp
                )
                messageRepo.sendGroupMessage(message, recentChat).collect {
                    when (it) {
                        is State.Success -> {
                            Log.d("zarea2", "in success   $ChatId")
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

    @RequiresApi(Build.VERSION_CODES.O)

    private fun updateRecent() {
        val currentTime: ZonedDateTime = now()

        viewModelScope.launch(Dispatchers.IO) {
            val recentChat = RecentChat(
                lastMessage =
                if (currentMessageState.value.fileTypes == "text") {
                    currentMessageState.value.message
                } else {
                    currentMessageState.value.fileName
                },
                timestamp = currentTime.toString(),

                )
            Log.d("zarea2", "calling update recent in vm  $ChatId")
            messageRepo.updateRecentChat(recentChat, ChatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("zarea2", "successful with data ${it.data}")
                        currentMessageState.value =
                            currentMessageState.value.copy(error = "recent updated")
                        Log.d("zarea2", "data in state before ${currentMessageState.value}")
                        currentMessageState.value = MessageState()
                        Log.d("zarea2", "data in state after ${currentMessageState.value}")
                    }

                    is State.Error -> {
                        currentMessageState.value =
                            currentMessageState.value.copy(error = it.message)
                    }

                    is State.Loading -> {
                        currentMessageState.value =
                            currentMessageState.value.copy(error = "recent updating")
                    }

                    else -> {}
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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