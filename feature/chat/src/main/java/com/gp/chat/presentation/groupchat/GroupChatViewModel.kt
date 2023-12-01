package com.gp.chat.presentation.groupchat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.chat.presentation.privateChat.MessageState
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.DateUtils.getTimeStamp
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
class GroupChatViewModel @Inject constructor (
    private val messageRepo: MessageRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _chatMessagesFlow = MutableStateFlow(emptyList<Message>())
    val chatMessagesFlow = _chatMessagesFlow.asStateFlow()
    val currentMessageState = MutableStateFlow(MessageState())
    private val currentUser= MutableStateFlow(NetworkUser())
    init{
        getCurrentUser()
    }
    fun fetchGroupChatMessages(groupId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.fetchGroupChatMessages(groupId).collect {
                Log.d("seerde", "Messages Received:${_chatMessagesFlow.value}")
                _chatMessagesFlow.value = it
                Log.d("seerde", "Messages Accepted")
            }
        }
    }

    fun onSendMessage(groupId: String) {
        val message = Message(
            id = "",
            groupId = groupId,
            message = currentMessageState.value.message,
            messageDate = SimpleDateFormat("MMMM dd, yyyy").format(Date()),
            senderId = currentUser.value.userEmail,
            senderName = currentUser.value.userFirstName + currentUser.value.userLastName,
            senderPfpURL = currentUser.value.userProfilePictureURL,
            timestamp = getTimeStamp(Date())
        )
        val recentChat = RecentChat(
            lastMessage = "${currentUser.value.userFirstName}: ${currentMessageState.value.message}",
            timestamp = message.timestamp
        )
        viewModelScope.launch (Dispatchers.IO){
            messageRepo.sendGroupMessage(message, recentChat).collect{
                when(it){
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
    fun deleteMessage(messageId: String, chatId: String){
        messageRepo.deleteMessage(messageId, chatId)
    }
    fun updateMessage(messageId: String, chatId: String,updatedText:String){
        messageRepo.updateMessage(messageId, chatId,updatedText)
    }
    fun getCurrentUser() {
        val currentUserId = Firebase.auth.currentUser?.email!!
        viewModelScope.launch (Dispatchers.IO) {
            when(userRepo.fetchUser(currentUserId)){
                is State.SuccessWithData -> {
                    currentUser.value = (userRepo.fetchUser(currentUserId) as State.SuccessWithData<NetworkUser>).data
                }
                else-> {}
            }
        }
    }

}