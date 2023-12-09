package com.gp.chat.presentation.createGroupChat

import com.gp.users.model.User

data class CreateGroupChatUiState(
    var name: String = "",
    var avatarURL: String = "https://firebasestorage.googleapis.com/v0/b/social-platform-3e56e.appspot.com/o/USERSPIC%2FAIW_Online_1_Sheet_SpiderMan_v1_sm-M.jpg?alt=media&token=af61c1aa-ad5f-478e-9bb8-ac95e7c96cff",
    var selectedMembers: List<User> = emptyList()
)
