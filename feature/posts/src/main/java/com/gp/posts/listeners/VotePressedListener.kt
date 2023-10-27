package com.gp.posts.listeners

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.Reply

interface VotePressedListener {

    fun onUpVotePressed(comment: Reply)
    fun onDownVotePressed(comment:Reply)
}