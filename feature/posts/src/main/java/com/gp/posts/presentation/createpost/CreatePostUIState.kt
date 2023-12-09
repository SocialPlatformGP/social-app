package com.gp.posts.presentation.createpost

import android.net.Uri
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostFile
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
    var files: List<PostFile> = emptyList()
)
