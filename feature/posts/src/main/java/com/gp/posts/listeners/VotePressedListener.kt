package com.gp.posts.listeners

import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity

interface VotePressedListener {

    fun onUpVotePressed(comment:ReplyEntity)
    fun onDownVotePressed(comment:ReplyEntity)
}