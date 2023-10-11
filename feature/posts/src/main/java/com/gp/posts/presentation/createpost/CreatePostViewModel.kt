package com.gp.posts.presentation.createpost

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
@HiltViewModel
class CreatePostViewModel @Inject constructor (private val postRepository: PostRepository) : ViewModel() {
    val uiState = MutableStateFlow(CreatePostUIState())
    private val currentUserName = "User 1"
    @RequiresApi(Build.VERSION_CODES.O)
    fun onCreatePost(){
        viewModelScope.launch {
            with(uiState.value) {
                postRepository.createPost(Post(currentUserName, LocalDateTime.now(), title, body, 0, 0, false))
            }
        }
    }
}