package com.gp.posts.presentation.createpost

import com.gp.socialapp.model.Tag
import com.gp.socialapp.utils.State

data class CreatePostUIState(
    var userProfilePicURL: String = "",
    var title: String = "",
    var body: String = "",
    var createdState: State<Nothing> = State.Idle,
    var cancelPressed: Boolean = false,
    var tags: List<Tag> = emptyList(),
    var type: String = "all",
)
