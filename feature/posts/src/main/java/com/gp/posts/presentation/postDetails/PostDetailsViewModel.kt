package com.gp.posts.presentation.postDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.NestedReplyItem
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.util.ReplyMapper.toNetworkModel
import com.gp.socialapp.util.ToNestedReplies.toNestedReplies
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val replyRepository: ReplyRepository
): ViewModel(){
    var collapsedReplies = mutableListOf<String>()

    private val _currentReplies = MutableStateFlow(NestedReplyItem(null, emptyList()))
    val currentReplies get() = _currentReplies.asStateFlow()

    private val _currentPost = MutableStateFlow<Post?>(null)
    val currentPost get() = _currentPost.asStateFlow()


    fun setThePost(post: Post){
        _currentPost.value = post
    }

    fun getRepliesById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.getReplies(id).collect { replies ->
                val updatedReplies = replies.map { reply ->
                    if (collapsedReplies.contains(reply.id)) {
                        reply.copy(collapsed = true)
                    } else {
                        reply
                    }
                }
                _currentReplies.value = updatedReplies.toNestedReplies()
            }
        }
    }

    fun insertReply(reply: Reply){
        viewModelScope.launch (Dispatchers.IO){
            replyRepository.insertReply(reply)
        }
    }
    fun upVote(post: Post){
        //TODO: implement upvote
    }
    fun downVote(post: PostEntity){
        //TODO: implement downvote
    }
    fun deletePost(post: Post){
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.deletePost(post)
        }
    }
    fun updatePost(post: Post){
        viewModelScope.launch (Dispatchers.IO){
            postRepository.updatePost(post)
        }
    }
    fun replyUpVote(reply: Reply){
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.upVoteReply(reply)
        }
    }
    fun replyDownVote(reply: Reply){
        viewModelScope.launch(Dispatchers.IO) {
            replyRepository.downVoteReply(reply)
        }
    }

    fun deleteReply(reply: Reply) {
        viewModelScope.launch(Dispatchers.IO) {
            var deletedReply = reply.copy(content = "content is deleted by owner",deleted = true)
            replyRepository.deleteReply(deletedReply)
        }

    }

    fun updateReply(reply: Reply) {
        viewModelScope.launch (Dispatchers.IO){
            replyRepository.updateReply(reply)
        }
    }


}