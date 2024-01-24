package com.gp.chat.presentation.groupdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.chat.model.ChatGroup
import com.gp.chat.repository.MessageRepository
import com.gp.chat.util.RemoveSpecialChar
import com.gp.socialapp.utils.State
import com.gp.users.model.User
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val messageRepo: MessageRepository
) : ViewModel() {
    private val _avatarURL = MutableStateFlow<String>("")
    val avatarURL = _avatarURL.asStateFlow()
    private val _groupName = MutableStateFlow<String>("")
    val groupName = _groupName.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()
    private val groupDetails = MutableStateFlow(ChatGroup())
    private lateinit var groupID: String
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