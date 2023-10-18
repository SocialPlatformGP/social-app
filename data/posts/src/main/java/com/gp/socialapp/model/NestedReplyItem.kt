package com.gp.socialapp.model

import com.gp.socialapp.database.model.ReplyEntity

data class NestedReplyItem(
    val reply: ReplyEntity?,
    var replies: List<NestedReplyItem>
)
