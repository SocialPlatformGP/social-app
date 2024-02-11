package com.gp.posts.presentation.postDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.posts.presentation.feedUiEvents.ReplyEvent
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.util.ToNestedReplies.toNestedReplies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val replyRepository: ReplyRepository
) : ViewModel() {
    var currentUser = Firebase.auth.currentUser
    var collapsedReplies = mutableListOf<String>()

    private val _currentReplies = MutableStateFlow(NestedReplyItem(null, emptyList()))
    val currentReplies = _currentReplies.asStateFlow()

    private val _currentPost = MutableStateFlow(
        Post(
            authorEmail = "",
            title = "",
            body = "",
            publishedAt = "",
            attachments = emptyList()
        )
    )
    val currentPost get() = _currentPost.asStateFlow()


    fun getPost(post: Post) {
        _currentPost.value = post
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.fetchPostById(post.id).collect {
                _currentPost.value = it
            }
        }
    }


    fun getRepliesById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.getReplies(id).collect { replies ->
                _currentReplies.value = replies.toNestedReplies()
                Log.d("inTopVM", "PostDetailsScreen: ${currentReplies.value}")

            }
        }
    }

    fun insertReply(reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.insertReply(reply)
        }
    }

    fun upVote(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.upVotePost(post)
        }
    }

    fun downVote(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.downVotePost(post)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.deletePost(post)
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.updatePost(post)
        }
    }

    fun replyUpVote(reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.upVoteReply(reply)
        }
    }

    fun replyDownVote(reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.downVoteReply(reply)
        }
    }

    fun deleteReply(reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            var deletedReply = reply.copy(content = "content is deleted by owner", deleted = true)
            replyRepository.deleteReply(deletedReply)
        }

    }

    fun updateReply(reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.updateReply(reply)
        }
    }

    fun handlePostEvent(event: PostEvent) {
        when (event) {
            is PostEvent.OnPostDeleted -> deletePost(event.post)
            is PostEvent.OnPostUpVoted -> upVote(event.post)
            is PostEvent.OnPostDownVoted -> downVote(event.post)
            is PostEvent.onCommentAdded->{
                val reply =  Reply(
                    postId = currentPost.value.id,
                    parentReplyId = null,
                    depth = 0,
                    content = event.text,
                    createdAt = Date().toString(),
                    authorEmail = currentUser?.email.toString()
                )
                insertReply(reply)
            }
            else -> {}
        }
    }
    fun handleReplyEvent(event: ReplyEvent) {
        var currentReply : Reply? = null
        when (event) {
            is ReplyEvent.OnReplyDeleted -> deleteReply(event.reply)
            is ReplyEvent.OnReplyUpVoted -> replyUpVote(event.reply)
            is ReplyEvent.OnReplyDownVoted -> replyDownVote(event.reply)
            is ReplyEvent.OnAddReply -> {currentReply = event.reply}
            is ReplyEvent.OnReplyAdded -> {
                val reply =  Reply(
                    postId = currentPost.value.id,
                    parentReplyId = currentReply?.id,
                    depth = currentReply?.depth?.plus(1)?:0,
                    content = event.text,
                    createdAt = Date().toString(),
                    authorEmail = currentUser?.email.toString()
                )
                insertReply(reply)
            }
            else -> {}
        }
    }


}