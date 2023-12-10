package com.gp.chat.presentation.privateChat

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.DateUtils.getTimeStamp
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
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
) : ViewModel() {


    private var currentEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)
    private var chatId = "-1"
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
        this.chatId = chatId
        this.senderName = senderName
        this.receiverName = receiverName
        this.senderPic = senderPic
        this.receiverPic = receiverPic
        getMessages()

    }


    private fun getMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.getMessages(chatId).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _messages.value = it.data
                    }

                    is State.Error -> {
                        if (it.message == "No messages found") {
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
                                ), chatId
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sendMessage() {
        if (currentMessage.value.message.isEmpty() && currentMessage.value.fileTypes == "text") {
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val message = Message(
                    senderId = currentEmail,
                    senderName = currentUser?.displayName!!,
                    senderPfpURL = currentUser?.photoUrl.toString(),
                    groupId = chatId,
                    messageDate = SimpleDateFormat("MMMM dd, yyyy").format(Date()),
                    message = currentMessage.value.message,
                    timestamp = getTimeStamp(Date()),
                    fileURI = currentMessage.value.fileUri ,
                    fileType = currentMessage.value.fileTypes ,
                    fileNames = currentMessage.value.fileName
                )

                messageRepository.sendMessage(message).collect {
                    if (it is State.SuccessWithData) {
                        updateRecent()
                    }

                }

            }
        }
    }

    fun sendFile(uri: Uri, type: String, fileName: String) {
        currentMessage.value = currentMessage.value.copy(
            fileName = fileName,
            fileUri = uri,
            fileTypes = type
        )
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
            messageRepository.updateRecentChat(recentChat, chatId).collect {
                if (it is State.SuccessWithData) {
                    currentMessage.value = MessageState()
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