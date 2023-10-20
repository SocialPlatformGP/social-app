package com.gp.posts.presentation.postsfeed

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.NetworkReply
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class FeedPostViewModel @Inject constructor(
    val repository: PostRepository,
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
                _uiState.value = State.SuccessWithData(posts)
            }

        }
    }
    

    fun upVote(post: PostEntity){
        Log.d("im in viewmodel", "upVote: ${post.upvotes} ")

        //update the ui
        if(uiState.value is State.SuccessWithData) {
            val updatedPosts = (uiState.value as State.SuccessWithData<List<PostEntity>>).data.map {
                if (it.id == post.id) {
                    it.copy(upvotes = it.upvotes + 1)
                } else {
                    it
                }
            }
            _uiState.value = State.SuccessWithData(updatedPosts)

            // Update the Room database
            viewModelScope.launch(Dispatchers.IO) {

                repository.updateLocalPost(post.copy(upvotes = post.upvotes + 1))
                repository.updatePost(post.copy(upvotes = post.upvotes + 1))
            }
        }
    }
    fun downVote(post: PostEntity){
        //update the ui
        if(uiState.value is State.SuccessWithData) {
            val updatedPosts = (uiState.value as State.SuccessWithData<List<PostEntity>>).data.map {
                if (it.id == post.id) {
                    it.copy(upvotes = it.upvotes - 1)
                } else {
                    it
                }
            }
            _uiState.value = State.SuccessWithData(updatedPosts)

            // Update the Room database
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateLocalPost(post.copy(upvotes = post.upvotes - 1))
                repository.updatePost(post.copy(upvotes = post.upvotes - 1))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }

    fun deletePost(post: PostEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePost(post)
            repository.deleteLocalPost(post)
        }
    }





}