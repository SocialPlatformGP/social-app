package com.gp.posts.presentation.postsfeed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.util.DateUtils
import com.gp.socialapp.util.PostPopularityUtils
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.model.User
import com.gp.users.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VipFeedViewModel @Inject constructor(
    val repository: PostRepository,
    val userRepository: UserRepository
) : ViewModel() {

    var currentUser = MutableStateFlow(NetworkUser())

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
    fun getUserById(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
           val temp = userRepository.fetchUser(userId)
            when(temp){
                is State.SuccessWithData -> {
                    currentUser.value = temp.data
                }
                else->{}
            }
        }
    }
}