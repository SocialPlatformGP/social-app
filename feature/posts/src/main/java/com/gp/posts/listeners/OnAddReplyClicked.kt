package com.gp.posts.listeners

import com.gp.socialapp.model.NestedReplyItem

interface OnAddReplyClicked {
    fun onAddReplyClicked(reply: NestedReplyItem)
}