package com.gp.socialapp.model

import com.gp.socialapp.database.model.ReplyEntity

data class NestedReplyItem(
    var reply: Reply?,
    var replies: List<NestedReplyItem>
)
