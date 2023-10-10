package com.gp.posts.presentation.postsfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.util.PostState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedPostViewModel @Inject constructor(
    val repository: PostRepository
) : ViewModel() {
    init {
        getAllPosts()
    }

    private val _posts = MutableStateFlow <List<PostEntity>>(emptyList())
    val posts
        get() = _posts.asStateFlow()

    private val _dataStatus = MutableStateFlow<PostState>(PostState.Idle)
    val dataStatus
        get() = _dataStatus.asStateFlow()

    fun getAllPosts(){

        viewModelScope.launch(Dispatchers.IO) {
                _dataStatus.value = PostState.Loading

            repository.getAllLocalPosts().collect{posts->
                _posts.value=posts
                _dataStatus.value = PostState.Success(true)
            }

        }
    }
    

    fun upVote(post: PostEntity){
        //update the ui
        val updatedPosts = _posts.value.map {
            if (it.id == post.id) {
                it.copy(upvotes = it.upvotes + 1)
            } else {
                it
            }
        }
        _posts.value = updatedPosts

        // Update the Room database
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocalPost(post.copy(upvotes = post.upvotes + 1))
        }
    }
    fun downVote(post: PostEntity){
        //update the ui
        val updatedPosts = _posts.value.map {
            if (it.id == post.id) {
                it.copy(downvotes = it.downvotes + 1)
            } else {
                it
            }
        }
        _posts.value = updatedPosts

        // Update the Room database
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocalPost(post.copy(downvotes = post.downvotes + 1))
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }

}