package com.gp.posts.listeners

import com.gp.socialapp.database.model.ReplyEntity

interface OnReplyCollapsed {
    fun onReplyCollapsed(reply: ReplyEntity)
}