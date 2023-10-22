package com.gp.posts.listeners

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.Reply

interface OnReplyCollapsed {
    fun onReplyCollapsed(reply: Reply)
}