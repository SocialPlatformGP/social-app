package com.gp.posts.presentation.postsfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.utils.State
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



    private val _uiState = MutableStateFlow<State<List<PostEntity>>>(State.Idle)
    val uiState
        get() = _uiState.asStateFlow()

    fun getAllPosts(){

        viewModelScope.launch(Dispatchers.IO) {
                _uiState.value = State.Loading

            repository.getAllLocalPosts().collect{posts->
                _uiState.value = State.Success(posts)
            }

        }
    }
    

    fun upVote(post: PostEntity){
        //update the ui
        if(uiState.value is State.Success) {
            val updatedPosts = (uiState.value as State.Success<List<PostEntity>>).data.map {
                if (it.id == post.id) {
                    it.copy(upvotes = it.upvotes + 1)
                } else {
                    it
                }
            }
            _uiState.value = State.Success(updatedPosts)

            // Update the Room database
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateLocalPost(post.copy(upvotes = post.upvotes + 1))
            }
        }
    }
    fun downVote(post: PostEntity){
        //update the ui
        if(uiState.value is State.Success) {
            val updatedPosts = (uiState.value as State.Success<List<PostEntity>>).data.map {
                if (it.id == post.id) {
                    it.copy(downvotes = it.upvotes + 1)
                } else {
                    it
                }
            }
            _uiState.value = State.Success(updatedPosts)

            // Update the Room database
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateLocalPost(post.copy(downvotes = post.upvotes + 1))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }

}