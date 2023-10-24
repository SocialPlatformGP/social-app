package com.gp.posts.presentation.postsfeed

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class FeedPostViewModel @Inject constructor(
    val repository: PostRepository,
    val replyRepository: ReplyRepository
) : ViewModel() {
    init {
        getAllPosts()
    }


    private val _uiState = MutableStateFlow<State<List<Post>>>(State.Idle)
    val uiState
        get() = _uiState.asStateFlow()

    fun getAllPosts() {

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = State.Loading
            repository.getAllLocalPosts().collect { posts ->
                _uiState.value = State.SuccessWithData(posts)
            }
        }
    }




    fun upVote(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upVotePost(post)
        }
    }

    fun downVote(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.downVotePost(post)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }

    fun deletePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePost(post)
            repository.deleteLocalPost(post)
        }
    }


}