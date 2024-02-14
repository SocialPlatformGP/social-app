package com.gp.posts.presentation.postsfeed

import com.gp.socialapp.model.Post
import com.gp.socialapp.utils.State

data class FeedPostUIState (
    val posts :List<Post> = emptyList(),
    val state: State<Nothing> = State.Idle
)
