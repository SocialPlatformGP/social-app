package com.gp.chat.presentation.createGroupChat

import com.gp.users.model.User

data class CreateGroupChatUiState(
    var name: String = "",
    var avatarURL: String = "",
    var selectedMembers: List<User> = emptyList()
)
