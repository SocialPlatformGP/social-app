package com.gp.posts.listeners

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post

interface VotesClickedListener:PostOnClickListener {

    fun onUpVoteClicked(post: PostEntity)
    fun onDownVoteClicked(post: PostEntity)
}