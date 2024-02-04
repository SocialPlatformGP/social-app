package com.gp.posts.presentation.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.database.model.PostFile
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor (
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val uiState = MutableStateFlow(CreatePostUIState())
    private val currentUserName = Firebase.auth.currentUser?.email
    private val channelTags = MutableStateFlow(emptyList<Tag>())
    val tags = channelTags.asStateFlow()

    init{
        getCurrentUser()
        getChannelTags()
    }
    private val currentUser= MutableStateFlow(NetworkUser())

    fun getChannelTags(){
        viewModelScope.launch {
            postRepository.getAllTags().collect{
                channelTags.value = it
            }
        }
    }
    fun insertNewTags(tags: List<Tag>){
        viewModelScope.launch {
            tags.forEach {
                postRepository.insertTag(it)
            }
        }
    }
    fun onCreatePost(){
        viewModelScope.launch {
            with(uiState.value) {
                val state =
                    postRepository.createPost(Post(
                        userPfp = userProfilePicURL,
                        userName= "${currentUser.value.userFirstName} ${currentUser.value.userLastName}",
                        authorEmail = currentUserName!!,
                        publishedAt = Date().toString(),
                        title = title,
                        body = body,
                        tags = tags,
                        type = type,
                        attachments = emptyList()
                    ), uiState.value.files)
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
                   currentUser.value = (userRepository.fetchUser(currentUserName) as State.SuccessWithData<NetworkUser>).data
                   uiState.value = uiState.value.copy(userProfilePicURL = currentUser.value.userProfilePictureURL)
               }
               else-> {}
           }
        }
    }
    fun setType(type: String){
        uiState.value = uiState.value.copy(type = type)
    }

    fun addFile(postFile: PostFile) {
        viewModelScope.launch{
            uiState.update { it.copy(files = it.files + postFile) }
        }
    }

    fun removeFile(file: PostFile) {
        viewModelScope.launch(Dispatchers.Default) {
            val newFiles = uiState.value.files.filter { it.uri !=file.uri }
            uiState.value = uiState.value.copy(files = newFiles)
        }
    }

    fun onTitleChange(title: String) {
        uiState.update { it.copy(title = title) }
    }

    fun onBodyChange(body: String) {
        uiState.update { it.copy(body = body) }
    }

    fun onAddTag(tag: Set<Tag>) {
        uiState.update { it.copy(tags = it.tags + tag) }
    }
    fun onRemoveTag(tag: Tag) {
        uiState.update { it.copy(tags = it.tags - tag) }
    }

}