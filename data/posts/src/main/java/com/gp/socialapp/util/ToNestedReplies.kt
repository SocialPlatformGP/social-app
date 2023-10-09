package com.gp.socialapp.util

import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NestedReplyItem

object ToNestedReplies {
    private fun List<ReplyEntity>.toNestedReplies(postId: String): NestedReplyItem {
        val replies= this.filter { it.postId == postId }
        val nestedRepliesList = buildNestedReplies(replies, null)
        return NestedReplyItem(null, replies = nestedRepliesList)
    }

    private fun buildNestedReplies(
        replies: List<ReplyEntity>,
        parentReplyId: Long?
    ): List<NestedReplyItem> {
        return replies
            .filter { it.parentReplyId == parentReplyId }
            .map { reply ->
                val nestedReplies = buildNestedReplies(replies, reply.id)
                NestedReplyItem(
                    reply = reply,
                    replies = nestedReplies
                )
            }
    }
}