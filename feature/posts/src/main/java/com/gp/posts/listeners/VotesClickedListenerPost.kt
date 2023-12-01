package com.gp.posts.listeners

import com.gp.socialapp.model.Post

interface VotesClickedListenerPost:OnPostClickListener {

    fun onUpVoteClicked(post: Post)
    fun onDownVoteClicked(post: Post)
}