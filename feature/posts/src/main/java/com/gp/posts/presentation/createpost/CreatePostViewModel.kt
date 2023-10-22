package com.gp.posts.presentation.createpost

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor (
    private val postRepository: PostRepository
) : ViewModel() {
    val uiState = MutableStateFlow(CreatePostUIState())
    private val pfpLink = "https://clipart-library.com/data_images/6103.png"
    private val currentUserName = Firebase.auth.currentUser?.email
    init{
        uiState.value.userProfilePicURL = pfpLink
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onCreatePost(){
        viewModelScope.launch {
            with(uiState.value) {
                val state =
                    postRepository.createPost(Post(
                        authorEmail = currentUserName!!,
                        publishedAt = LocalDateTime.now().toString(),
                        title = title,
                        body = body
                    ))
                state.collect{newState ->
                    uiState.value = uiState.value.copy(createdState = newState)
                }
            }
            delay(500)
        }
    }
    fun onCancel(){
        uiState.value = uiState.value.copy(cancelPressed = true)
    }

    fun resetCancelPressed() {
        uiState.value.cancelPressed = false
    }
}