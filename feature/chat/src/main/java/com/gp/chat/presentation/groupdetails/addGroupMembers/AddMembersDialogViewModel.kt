package com.gp.chat.presentation.groupdetails.addGroupMembers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
class AddMembersDialogViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val chatRepo: MessageRepository
): ViewModel() {
    val uiState = MutableStateFlow(AddMembersUIState())
    private val _allUsers = MutableStateFlow(emptyList<User>())
    private val _users = MutableStateFlow(emptyList<SelectableUser>())
    val users = _users.asStateFlow()
    private val currentUserEmail = userRepo.getCurrentUserEmail()
    init{
        getListOfUsers()
    }
    private fun getListOfUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepo.fetchUsers().collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _allUsers.value = it.data.filter { it.email != currentUserEmail }
                        Log.d("seerde", "User add Members get all: ${_allUsers.value.map{it.email}}")
                        uiState.value = uiState.value.copy(isAllUsersLoaded = true)
                    }

                    is State.Error -> {
                        Log.e("seerde", "fetchUsers() error: ${it.message}")
                    }

                    else -> {
                        Log.d("seerde", "Loading from all users: ${it is State.Loading}")
                    }
                }
            }
        }
    }
    fun submitGroupUsers(groupUsers: List<User>){
        viewModelScope.launch (Dispatchers.Default){
            _users.value = _allUsers.value.filter { !(groupUsers.contains(it)) }.map{it.toSelectableUser()}
            Log.d("seerde", "wtf???not joined User in add members viewmodel: ${_users.value.map{it.data.email}}")
        }
    }
    fun addMember(user: User) {
        viewModelScope.launch (Dispatchers.Default){
            val updatedUsers = uiState.value.selectedUsers.toMutableList()
            updatedUsers.add(user)
            _users.value = _users.value.map {
                if (it.data.email == user.email) {
                    Log.d("seerde", "User status before edit in viewmodel: ${it.isSelected}")
                    it.copy(isSelected = true)
                } else {
                    it
                }
            }
            Log.d("seerde", "User status before edit in viewmodel: ${_users.value.find { it.data.email == user.email }!!.isSelected}")
            uiState.value = uiState.value.copy(selectedUsers = updatedUsers.toList())
        }
    }

    fun removeMember(user: User) {
        viewModelScope.launch(Dispatchers.Default){
            val updatedUsers =uiState.value.selectedUsers.toMutableList()
            updatedUsers.remove(user)
            _users.value = _users.value.map {
                if (it.data.email == user.email) {
                    it.copy(isSelected = false)
                } else {
                    it
                }
            }
            uiState.value = uiState.value.copy(selectedUsers = updatedUsers.toList())
        }
    }
    fun onAddMembersClick(groupId: String){
        viewModelScope.launch{
            val userEmails = uiState.value.selectedUsers.map{it.email}
            Log.d("seerde" , "User emails to add: ${userEmails}")
            chatRepo.addGroupMembers(groupId, userEmails).collect{
                when(it){
                    is State.Success -> {
                        uiState.value = uiState.value.copy(isCreated = true)
                    }
                    is State.Error -> {
                        Log.d("seerde", "Adding Members Failed: ${it.message}")
                    }
                    else ->{}
                }
            }
        }
    }
}