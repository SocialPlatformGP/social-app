package com.gp.chat.presentation.groupdetails

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.model.ChatGroup
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.utils.State
import com.gp.users.model.User
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val messageRepo: MessageRepository
) : ViewModel(){
    val currentUserEmail = Firebase.auth.currentUser!!.email!!
    private val _haveChatWithUserState = MutableStateFlow<State<String>>(State.Idle)
    val haveChatWithUserState = _haveChatWithUserState.asStateFlow()
    private val _avatarURL = MutableStateFlow<String>("")
    val avatarURL = _avatarURL.asStateFlow()
    private val _groupName = MutableStateFlow<String>("")
    val groupName = _groupName.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()
    private val _admins = MutableStateFlow<List<String>>(emptyList())
    val admins = _admins.asStateFlow()
    private val groupDetails = MutableStateFlow(ChatGroup())
    lateinit var groupID: String
    fun getUsersList(key: String) {
        groupID = key
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.getGroupDetails(key).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("seerde", "1: ${it.data}")
                        groupDetails.value = it.data
                        _avatarURL.value = it.data.picURL
                        _groupName.value = it.data.name
                        _admins.value = it.data.members.filter { member -> member.value }.keys.toList()
                        userRepo.getUsersByEmails(
                            it.data.members.keys.map { RemoveSpecialChar.restoreOriginalEmail(it) }
                        ).collect {
                            when (it) {
                                is State.SuccessWithData -> {
                                    Log.d("seerde", "2: ${it.data}")
                                    _users.value = it.data
                                }

                                else -> {}
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateGroupName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.changeGroupName(groupID, name).collect {
                when (it) {
                    is State.Success -> {
                        Log.d("SEERDE", "updateGroupName: Success")
                    }

                    is State.Error -> {
                        Log.e("seerde", "Error in updating name: ${it.message}")
                    }

                    else -> {
                        Log.d("SEERDE", "updateGroupName: Other shit")
                    }
                }
            }
        }
    }
    fun updateAvatar(uri: Uri?){
        if(uri != null){
            viewModelScope.launch(Dispatchers.IO) {
                messageRepo.updateGroupAvatar(uri, avatarURL.value, groupID = groupID).collect{
                    when(it){
                        is State.SuccessWithData -> {
                            _avatarURL.value = it.data
                        }
                        is State.Error -> {
                            Log.e("SEERDE", "updateAvatar: ${it.message}")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun messageUser(userEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("SEERDE", "messageUser: call reached viewmodel")
            messageRepo.haveChatWithUser(
                removeSpecialCharacters(userEmail), removeSpecialCharacters(currentUserEmail)).collect{
                when (it) {
                    is State.SuccessWithData -> {
                        Log.d("SEERDE", "messageUser: success in viewmodel: data:${it.data}")
                        _haveChatWithUserState.value = State.SuccessWithData(it.data)
                    }
                    is State.Error -> {
                        Log.e("SEERDE", "messageUser: ${it.message}", )
                    }
                    else -> {}
                }
            }
        }
    }

    fun createNewChat(receiverEmail: String){
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.insertChat(
                ChatGroup(
                    name = "Private Chat",
                    members = mapOf(
                        removeSpecialCharacters(currentUserEmail) to true,
                        removeSpecialCharacters(receiverEmail) to true
                    )
                )
            ).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        insertUserToChat(removeSpecialCharacters(currentUserEmail), it.data,
                            removeSpecialCharacters(receiverEmail)
                        )
                        insertPrivateChat(receiverEmail, it.data)
                        _haveChatWithUserState.value = State.SuccessWithData(it.data)
                    }
                    is State.Error -> {
                        Log.e("SEERDE", "createNewChat: ${it.message}", )
                    }
                    else -> {}
                }

            }
        }
    }

    fun insertUserToChat(senderEmail: String, chatId: String, receiverEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.insertChatToUser(
                chatId,
                senderEmail,
                receiverEmail
            ).collect {
                if(it is State.Error) {
                    Log.e("SEERDE", "insertUserToChat: ", )
                }
            }
        }
    }

    fun insertPrivateChat(receiverEmail: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.insertPrivateChat(
                removeSpecialCharacters(currentUserEmail),
                removeSpecialCharacters(receiverEmail),
                chatId
            ).collect {
                if(it is State.Error) {
                    Log.e("SEERDE", "insertUserToChat: ", )
                }
            }
        }
    }

    fun removeGroupMember(key: String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.removeMemberFromGroup(key, user.email).collect {
                when (it) {
                    is State.Success -> {
                        Log.d("seerde", "User Removed Successfully")
                        getUsersList(key)
                    }
                    else -> {}
                }
            }
        }
    }
}