package com.gp.chat.presentation.groupdetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.gp.chat.repository.MessageRepository
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
class GroupDetailsViewModel @Inject constructor(private val userRepo: UserRepository,
    private val messageRepo: MessageRepository): ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    fun getUsersList(key: String) {
        viewModelScope.launch (Dispatchers.IO){
            messageRepo.getGroupMembersEmails(key).collect{
                when(it){
                    is State.SuccessWithData -> {
                        Log.d("seerde", "1: ${it.data}")
                        userRepo.getUsersByEmails(it.data).collect{
                            when(it) {
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

    fun removeGroupMember(key:String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepo.removeMemberFromGroup(key, user.email).collect{
                when(it){
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