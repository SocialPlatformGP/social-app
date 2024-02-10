package com.gp.posts.presentation.editPostContent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {


    val uiState = MutableStateFlow(EditPostUIState())
    val post = MutableStateFlow<Post?>(null)

    fun updatePost() {
        with(uiState.value) {
            Log.d("EditPostViewModel", "updatePost: $title $body")
            viewModelScope.launch(Dispatchers.IO) {
                post.value?.let {
                    postRepository.updatePost(
                        it.copy(
                            title = title,
                            body = body,
                        )
                    )
                }
            }
        }
    }
    fun onTitleChange (newTitle: String) {
        uiState.update { it.copy(title = newTitle) }
    }
    fun onBodyChange (newBody: String) {
        uiState.update { it.copy(body = newBody) }
    }
}

data class EditPostUIState(
    var title: String = "",
    var body: String = "",
)