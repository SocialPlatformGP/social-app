package com.gp.socialapp.model


data class NestedReplyItem(
    var reply: Reply?,
    var replies: List<NestedReplyItem>
)
