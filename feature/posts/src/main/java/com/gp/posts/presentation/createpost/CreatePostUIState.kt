package com.gp.posts.presentation.createpost

data class CreatePostUIState(
    var title: String,
    var body: String
) {
    constructor() : this("", "")
}
