package com.gp.posts.presentation.createpost

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor (
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val uiState = MutableStateFlow(CreatePostUIState())
    private val pfpLink = "https://clipart-library.com/data_images/6103.png"
    private val currentUserName = Firebase.auth.currentUser?.email
    init{
        uiState.value.userProfilePicURL = pfpLink
        getCurrentUser()
    }
    private val currentUser= MutableStateFlow(NetworkUser())

    fun onCreatePost(){
        viewModelScope.launch {
            with(uiState.value) {
                val state =
                    postRepository.createPost(Post(
                        userPfp = pfpLink,//todo: change to user pfp
                        userName= currentUser.value.userFirstName,//todo: change to user name to full name
                        authorEmail = currentUserName!!,
                        publishedAt = Date().toString(),
                        title = title,
                        body = body,
                        tags = tags
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
    fun getCurrentUser() {
        viewModelScope.launch (Dispatchers.IO) {
           when(userRepository.fetchUser(currentUserName!!)){
               is State.SuccessWithData -> {
                   currentUser.value = (userRepository.fetchUser(currentUserName!!) as State.SuccessWithData<NetworkUser>).data!!
               }
               else-> {}
           }
        }
    }
}