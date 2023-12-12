package com.gp.chat.presentation.groupdetails.addGroupMembers

import com.gp.users.model.User

data class AddMembersUIState(
    var isCreated: Boolean = false,
    var isAllUsersLoaded: Boolean = false,
    val selectedUsers: List<User> = emptyList()
)
