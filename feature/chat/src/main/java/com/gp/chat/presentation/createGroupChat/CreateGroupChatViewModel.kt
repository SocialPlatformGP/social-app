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
import javax.inject.Inject

@HiltViewModel
class CreateGroupChatViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val chatRepo: MessageRepository
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private val _avatarURL = MutableStateFlow("")
    val avatarURL = _avatarURL.asStateFlow()
    private val _selectedUsers = MutableStateFlow(emptyList<User>())
    val selectedUsers = _selectedUsers.asStateFlow()
    private val _users = MutableStateFlow(emptyList<SelectableUser>())
    val users = _users.asStateFlow()
    private val currentUserEmail = userRepo.getCurrentUserEmail()

    init {
        Log.d("EDREES", "Viewmodel Initialized")
        getListOfUsers()
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateAvatarURL(url: String) {
        _avatarURL.value = url
    }

    fun addMember(user: User) {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedMembers = _selectedUsers.value.toMutableList()
            updatedMembers.add(user)
            _users.value = _users.value.map {
                if (it.data.email == user.email) {
                    Log.d("seerde", "User status before edit in viewmodel: ${it.isSelected}")
                    it.copy(isSelected = true)
                } else {
                    it
                }
            }
            Log.d(
                "seerde",
                "User status before edit in viewmodel: ${_users.value.find { it.data.email == user.email }!!.isSelected}"
            )
            _selectedUsers.value = updatedMembers.toList()
        }
    }

    fun removeMember(user: User) {
        viewModelScope.launch(Dispatchers.Default) {
            val updatedMembers = _selectedUsers.value.toMutableList()
            updatedMembers.remove(user)
            _users.value = _users.value.map {
                if (it.data.email == user.email) {
                    it.copy(isSelected = false)
                } else {
                    it
                }
            }
            _selectedUsers.value = updatedMembers.toList()
        }
    }

    fun getListOfUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.fetchUsers().collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _users.value = it.data.map { it.toSelectableUser() }
                            .filter { it.data.email != currentUserEmail }
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
        name = _name.value,
        avatarLink = _avatarURL.value,
        members = _selectedUsers.value.map { it.email },
        currentUserEmail = currentUserEmail
    )
}