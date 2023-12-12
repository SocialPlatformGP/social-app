package com.gp.chat.presentation.createGroupChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.chat.repository.MessageRepository
import com.gp.socialapp.utils.State
import com.gp.users.model.SelectableUser
import com.gp.users.model.SelectableUser.Companion.toSelectableUser
import com.gp.users.model.User
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateGroupChatViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val chatRepo: MessageRepository
) : ViewModel() {
    val uiState = MutableStateFlow(CreateGroupChatUiState())
    private val _users = MutableStateFlow(emptyList<SelectableUser>())
    val users = _users.asStateFlow()
    private val currentUserEmail = userRepo.getCurrentUserEmail()
    init {
        Log.d("EDREES", "Viewmodel Initialized")
        getListOfUsers()
    }

    fun addMember(user: User) {
        viewModelScope.launch (Dispatchers.Default){
            val updatedMembers = uiState.value.selectedMembers.toMutableList()
            updatedMembers.add(user)
            _users.value = _users.value.map {
                if (it.data.email == user.email) {
                    Log.d("seerde", "User status before edit in viewmodel: ${it.isSelected}")
                    it.copy(isSelected = true)
                } else {
                    it
                }
            }
            Log.d("seerde", "User status before edit in viewmodel: ${_users.value.find { it.data.email == user.email }!!.isSelected}")
            uiState.value = uiState.value.copy(selectedMembers = updatedMembers.toList())
        }
    }

    fun removeMember(user: User) {
        viewModelScope.launch(Dispatchers.Default){
            val updatedMembers = uiState.value.selectedMembers.toMutableList()
            updatedMembers.remove(user)
            _users.value = _users.value.map {
                if (it.data.email == user.email) {
                    it.copy(isSelected = false)
                } else {
                    it
                }
            }
            uiState.value = uiState.value.copy(selectedMembers = updatedMembers.toList())
        }
    }

    fun getListOfUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.fetchUsers().collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _users.value = it.data.map{it.toSelectableUser()}.filter { it.data.email != currentUserEmail }
                        Log.d("EDREES", "User: ${_users.value.map { it.data.email }}")
                    }

                    is State.Error -> {
                        Log.e("EDREES", "fetchUsers() error: ${it.message}")
                    }

                    else -> {
                        Log.d("Edrees", "Loading from all users: ${it is State.Loading}")
                    }
                }
            }
            Log.d("EDREES", "After fetchUsers()")
        }
    }


    fun createGroup() = chatRepo.createGroupChat(
        name = uiState.value.name,
        avatarLink = uiState.value.avatarURL,
        members = uiState.value.selectedMembers.map { it.email }, currentUserEmail)
}