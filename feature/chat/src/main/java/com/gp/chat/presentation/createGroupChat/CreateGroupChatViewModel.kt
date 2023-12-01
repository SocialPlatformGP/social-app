package com.gp.chat.presentation.createGroupChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.chat.repository.MessageRepository
import com.gp.socialapp.utils.State
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
    private val _users = MutableStateFlow(emptyList<User>())
    val users = _users.asStateFlow()
    private val currentUserEmail = userRepo.getCurrentUserEmail()
    private val URL =
        "https://www.schoolbag.edu.sg/images/default-source/story-images/entry-to-polytechnic-via-the-pfp-a-student-s-perspective/pfp-(1).jpg"

    init {
        Log.d("EDREES", "Viewmodel Initialized")
        getListOfUsers()
    }

    fun addMember(user: User) {
        val updatedMembers = uiState.value.selectedMembers.toMutableList()
        updatedMembers.add(user)
        uiState.value = uiState.value.copy(selectedMembers = updatedMembers.toList())
    }

    fun removeMember(user: User) {
        val updatedMembers = uiState.value.selectedMembers.toMutableList()
        updatedMembers.remove(user)
        uiState.value = uiState.value.copy(selectedMembers = updatedMembers.toList())
    }

    fun getListOfUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("EDREES", "GetListOfUsers Called")
            Log.d("EDREES", "Before fetchUsers()")
            userRepo.fetchUsers().collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _users.value = it.data.filter { it.email != currentUserEmail }
                        Log.d("EDREES", "User: ${_users.value.map { it.email }}")
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


    fun createGroup() = chatRepo.createGroupChat(name = uiState.value.name,
        avatarLink = URL,
        members = uiState.value.selectedMembers.map { it.email }, currentUserEmail)
}