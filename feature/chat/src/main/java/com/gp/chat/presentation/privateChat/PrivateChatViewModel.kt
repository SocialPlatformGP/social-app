package com.gp.chat.presentation.privateChat

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
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
class PrivateChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private var currentEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    private var ChatId = "-1"
    private var senderName = ""
    private var currentUser = Firebase.auth.currentUser
    private var receiverName = ""
    private var senderPic = ""
    private var receiverPic = ""
    val currentMessage = MutableStateFlow(MessageState())
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun setData(
        chatId: String,
        senderName: String,
        receiverName: String,
        senderPic: String,
        receiverPic: String
    ) {
        ChatId = chatId
        this.senderName = senderName
        this.receiverName = receiverName
        this.senderPic = senderPic
        this.receiverPic = receiverPic
        getMessages()

    }


    fun getMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getMessages(ChatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _messages.value = it.data
                        currentMessage.value = currentMessage.value.copy(error = "data loaded")

                    }

                    is State.Error -> {
                        currentMessage.value = currentMessage.value.copy(error = it.message)
                        if (it.message == "No messages found") {
                            Log.d("zarea4","no message found"+it.message)
                            messageRepository.insertRecentChat(
                                RecentChat(
                                    id = "",
                                    senderName = senderName,
                                    senderPicUrl = senderPic,
                                    receiverName = receiverName,
                                    receiverPicUrl = receiverPic,
                                    lastMessage = "No messages yet",
                                    timestamp = getTimeStamp(Date()),
                                    title = "private chat",
                                    privateChat = true
                                ), ChatId
                            )
                        }
                    }

                    is State.Loading -> {
                        currentMessage.value =
                            currentMessage.value.copy(error = " getting messages")
                    }

                    else -> {}
                }
            }
        }
    }

    fun sendMessage() {
        if (currentMessage.value.message.isEmpty() && currentMessage.value.fileTypes == "text") {
            currentMessage.value = currentMessage.value.copy(error = "message is empty")
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {

                val message = Message(
                    senderId = currentEmail,
                    senderName = currentUser?.displayName!!,
                    senderPfpURL = currentUser?.photoUrl.toString(),
                    groupId = ChatId,
                    messageDate = SimpleDateFormat("MMMM dd, yyyy").format(Date()),
                    message = currentMessage.value.message,
                    timestamp = getTimeStamp(Date()),
                    fileURI = currentMessage.value.fileUri ?: "".toUri(),
                    fileType = currentMessage.value.fileTypes ?: "",
                    fileNames = currentMessage.value.fileName ?: ""
                )

                messageRepository.sendMessage(message).collect {
                    when (it) {
                        is State.SuccessWithData -> {
                            updateRecent()
                        }

                        is State.Error -> {
                            currentMessage.value = currentMessage.value.copy(error = it.message)
                        }

                        is State.Loading -> {
                            currentMessage.value =
                                currentMessage.value.copy(error = "sending message")
                        }

                        else -> {}
                    }
                }

            }
        }
    }

    fun sendFile(uri: Uri, type: String, fileName: String) {
        currentMessage.value = currentMessage.value.copy(fileName = fileName)
        currentMessage.value = currentMessage.value.copy(fileUri = uri)
        currentMessage.value = currentMessage.value.copy(fileTypes = type)
        sendMessage()
    }

    private fun updateRecent() {
        viewModelScope.launch(Dispatchers.IO) {
            val recentChat = RecentChat(
                lastMessage =
                if (currentMessage.value.fileTypes == "text") {
                    currentMessage.value.message
                } else {
                    currentMessage.value.fileName
                },
                timestamp = Date().toString(),
                title = "private chat",
                privateChat = true,
                receiverName = receiverName,
                senderName = senderName,
                receiverPicUrl = receiverPic,
                senderPicUrl = senderPic,


                )
            messageRepository.updateRecentChat(recentChat, ChatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        currentMessage.value = currentMessage.value.copy(error = "recent updated")
                        currentMessage.value = MessageState()
                    }

                    is State.Error -> {
                        currentMessage.value = currentMessage.value.copy(error = it.message)
                    }

                    is State.Loading -> {
                        currentMessage.value = currentMessage.value.copy(error = "recent updated")
                    }

                    else -> {}
                }
            }
        }
    }


    fun deleteMessage(messageId: String, chatId: String) {
        messageRepository.deleteMessage(messageId, chatId)
    }

    fun updateMessage(messageId: String, chatId: String, updatedText: String) {
        messageRepository.updateMessage(messageId, chatId, updatedText)
    }

}