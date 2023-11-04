package com.gp.chat.presentation.groupchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.GroupMessage
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor (
    private val messageRepo: MessageRepository,
    private val userRepo: UserRepository
) : ViewModel() {
    private val _chatMessagesFlow = MutableStateFlow(emptyList<GroupMessage>())
    val chatMessagesFlow = _chatMessagesFlow.asStateFlow()
    val currentMessageState = MutableStateFlow(MessageState())
    private val currentUser= MutableStateFlow(NetworkUser())
    init{
        getCurrentUser()
    }
    fun fetchGroupChatMessages(groupId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.fetchGroupChatMessages(groupId).collectLatest {
                _chatMessagesFlow.value = it
            }
        }
    }

    fun onSendMessage(groupId: String) {
        val message = GroupMessage(
            id = "",
            message = currentMessageState.value.message,
            messageDate = Date().toString(),
            senderId = currentUser.value.userEmail,
            senderName = currentUser.value.userFirstName + currentUser.value.userLastName,
            senderPfpURL = currentUser.value.userProfilePictureURL,
            timestamp = getTimeStamp(Date())
        )
        messageRepo.sendGroupMessage(groupId, message)
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