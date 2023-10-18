package com.gp.posts.presentation.createpost

import com.gp.socialapp.utils.State

data class CreatePostUIState(
    var title: String,
    var body: String,
    var createdState:State<Nothing>
) {
    constructor() : this("", "", State.Idle)
}
