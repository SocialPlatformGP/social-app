package com.gp.posts.presentation.createpost

import com.gp.socialapp.utils.State

data class CreatePostUIState(

    var userProfilePicURL: String,
    var title: String,
    var body: String,
    var createdState: State<Nothing>,
    var cancelPressed: Boolean
) {
    constructor() : this("", "", "", State.Idle, false)
}
