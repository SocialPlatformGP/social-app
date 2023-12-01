package com.gp.posts.presentation.postsfeed

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.util.PostPopularityUtils
import com.gp.socialapp.util.DateUtils
import com.gp.socialapp.utils.State
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class FeedPostViewModel @Inject constructor(
    val repository: PostRepository,
    val replyRepository: ReplyRepository,
    val userRepository: UserRepository
) : ViewModel() {
    init {
        getAllPosts()
    }


    private val _uiState = MutableStateFlow<State<List<Post>>>(State.Idle)
    val uiState
        get() = _uiState.asStateFlow()
    val isSortedByNewest = MutableStateFlow(true)

    fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = State.Loading
            repository.getAllLocalPosts().collect { posts ->
                val sortedPosts = if(isSortedByNewest.value){
                    posts.sortedByDescending { DateUtils.convertStringToDate(it.publishedAt) }
                } else {
                    posts.sortedByDescending { PostPopularityUtils.calculateInteractionValue(it.votes, it.replyCount)}
                }
                _uiState.value = State.SuccessWithData(sortedPosts)
                Log.d("TAG258", "New Data: ${sortedPosts}")
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

    fun sortPostsByNewest() {
        viewModelScope.launch{
            isSortedByNewest.value = true
            val sortedPosts = (uiState.value as State.SuccessWithData).data
                .sortedByDescending { DateUtils.convertStringToDate(it.publishedAt) }
            _uiState.value = State.SuccessWithData(sortedPosts)
            Log.d("TAG258", "New Data by newest: ${sortedPosts}")
        }
    }

    fun sortPostsByPopularity() {
        viewModelScope.launch{
            isSortedByNewest.value = false
            val sortedPosts = (uiState.value as State.SuccessWithData).data
                .sortedByDescending { PostPopularityUtils.calculateInteractionValue(it.votes, it.replyCount)}
            _uiState.value = State.SuccessWithData(sortedPosts)
            Log.d("TAG258", "New Data by popular: ${sortedPosts}")
        }
    }
}