package com.gp.posts.presentation.feedUiEvents

import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.model.Tag

sealed class PostEvent() {
    data class OnPostClicked(val post: Post) : PostEvent()
    data class OnPostDeleted(val post: Post) : PostEvent()
    data class OnPostEdited(val post: Post) : PostEvent()
    data class OnPostUpVoted(val post: Post) : PostEvent()
    data class OnPostDownVoted(val post: Post) : PostEvent()
    object OnAddPost : PostEvent()
    data class OnTagClicked(val tag: Tag) : PostEvent()
    data class OnAudioClicked(val attachment: PostAttachment) : PostEvent()
    data class OnImageClicked(val attachment: PostAttachment) : PostEvent()
    data class OnVideoClicked(val attachment: PostAttachment) : PostEvent()
    data class OnDocumentClicked(val attachment: PostAttachment) : PostEvent()
    data class OnCommentClicked(val post: Post) : PostEvent()

}

sealed class ReplyEvent {
    data class OnReplyClicked(val reply: Reply) : ReplyEvent()
    data class OnReplyDeleted(val reply: Reply) : ReplyEvent()
    data class OnReplyEdited(val reply: Reply) : ReplyEvent()
    data class OnReplyUpVoted(val reply: Reply) : ReplyEvent()
    data class OnReplyDownVoted(val reply: Reply) : ReplyEvent()
    data class OnAddReply(val reply: Reply) : ReplyEvent()
}