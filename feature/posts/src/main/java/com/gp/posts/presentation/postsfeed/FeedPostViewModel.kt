package com.gp.posts.presentation.postsfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.util.PostMapper.toNetworkModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedPostViewModel @Inject constructor(
    val repository: PostRepository
) : ViewModel() {
    init {
        //todo delete this dummy data
        getAllPosts()
    }

    private val _posts = MutableStateFlow <List<PostEntity>>(emptyList())
    val posts
        get() = _posts.asStateFlow()

    fun getAllPosts(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllLocalPosts().collect{posts->
                _posts.value=posts
            }
        }
    }
    fun insertDummy(data:List<PostEntity>){
        viewModelScope.launch(Dispatchers.IO) {
            data.forEach {
                repository.insertLocalPost(it)
            }
        }
    }
    fun upVote(post: PostEntity){
        //update the ui
        val updatedPosts = _posts.value.map {
            if (it.id == post.id) {
                it.copy(upvotes = it.upvotes + 1)
            } else {
                it
            }
        }
        _posts.value = updatedPosts

        // Update the Room database
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocalPost(post.copy(upvotes = post.upvotes + 1))
        }
    }
    fun downVote(post: PostEntity){
        //update the ui
        val updatedPosts = _posts.value.map {
            if (it.id == post.id) {
                it.copy(downvotes = it.downvotes + 1)
            } else {
                it
            }
        }
        _posts.value = updatedPosts

        // Update the Room database
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLocalPost(post.copy(downvotes = post.downvotes + 1))
        }
    }

}