package com.gp.posts.presentation.editPostContent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Tag
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {


    val _post = MutableStateFlow(Post())
    val post = _post.asStateFlow()
    private val channelTags = MutableStateFlow(emptyList<Tag>())
    val tags = channelTags.asStateFlow()

    fun updatePost() {
        with(post.value) {
            viewModelScope.launch(Dispatchers.IO) {
                postRepository.updatePost(
                    post.value.copy(
                        title = title,
                        body = body,
                    )
                )
            }
        }
    }

    fun getChannelTags() {
        viewModelScope.launch {
            postRepository.getAllTags().collect {
                channelTags.value = it
            }
        }
    }

    fun setPost(currentPost: Post) {
        _post.update { currentPost }
    }

    fun updateBody(body: String) {
        _post.update { it.copy(body = body) }
    }

    fun updateTitle(title: String) {
        _post.update { it.copy(title = title) }
    }

    fun addTag(tags: List<Tag>) {
        val tags2 = post.value.tags + tags
        _post.update { it.copy(tags = tags2.distinct()) }
    }

    fun removeTag(tag: Tag) {
        _post.update { it.copy(tags = it.tags - tag) }
    }

    fun addFile(files: List<PostAttachment>) {
        val files2 = post.value.attachments + files

        _post.update { it.copy(attachments = files2.distinct()) }
    }

    fun removeFile(file: PostAttachment) {
        _post.update { it.copy(attachments = it.attachments - file) }
    }

    fun handleEvent(event: EditPostEvents) {
        when (event) {
            is EditPostEvents.OnTitleChanged -> updateTitle(event.title)
            is EditPostEvents.OnContentChanged -> updateBody(event.content)
            is EditPostEvents.OnTagAdded -> addTag(event.tags.toList())
            is EditPostEvents.OnTagRemoved -> removeTag(event.tag)
            is EditPostEvents.OnFileRemoved -> removeFile(event.file)
            is EditPostEvents.OnFileAdded -> addFile(event.files)
            is EditPostEvents.OnApplyEditClicked -> updatePost()
            else -> Unit
        }
    }

}