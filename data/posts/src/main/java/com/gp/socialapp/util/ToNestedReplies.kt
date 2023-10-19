package com.gp.socialapp.util

import android.util.Log
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NestedReplyItem

object ToNestedReplies {
     fun List<ReplyEntity>.toNestedReplies(): NestedReplyItem {
        Log.d("ToNestedRepliesb", "toNestedReplies: ${this.size}")
        val nestedRepliesList = buildNestedReplies(this, null)
         Log.d("ToNestedRepliesa", "toNestedReplies: ${this.size}")

         return NestedReplyItem(null, replies = nestedRepliesList)


     }

    private fun buildNestedReplies(
        replies: List<ReplyEntity>,
        parentReplyId: String?
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