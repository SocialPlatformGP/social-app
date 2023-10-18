package com.gp.posts.presentation.createpost

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.util.PostMapper.toNetworkModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
@HiltViewModel
class CreatePostViewModel @Inject constructor (
    private val postRepository: PostRepository
) : ViewModel() {

    val uiState = MutableStateFlow(CreatePostUIState())
    private val currentUserName = "User 1"
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    fun onCreatePost(){
        val post =with(uiState.value) {
             postRepository.createNetworkPost(Post(currentUserName, LocalDateTime.now(), title, body, 0, 0, false).toNetworkModel(1))
        }
        viewModelScope.launch {
            post.collect {
                uiState.value = uiState.value.copy(createdState = it)
            }
        }
    }

}