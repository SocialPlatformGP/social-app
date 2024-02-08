package com.gp.posts.presentation.editPostContent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.posts.presentation.createpost.CreatePostUIState
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {


data class EditPostUIState(
    var title: String = "",
    var body: String = "",
)

    val uiState = MutableStateFlow<EditPostUIState>(EditPostUIState())
    val post = MutableStateFlow<Post?>(null)

    fun updatePost(){
        with(uiState.value){
            Log.d("EditPostViewModel", "updatePost: $title $body")
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.updatePost(post.value!!.copy(
                title = title,
                body = body,
            ))
        }
    }}
    fun updateBody(body: String) {
        uiState.value = uiState.value.copy(body = body)
    }
    fun updateTitle(title: String) {
        uiState.value = uiState.value.copy(body = title)
    }

}