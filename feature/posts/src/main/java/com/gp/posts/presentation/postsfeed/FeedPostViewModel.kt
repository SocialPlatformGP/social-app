package com.gp.posts.presentation.postsfeed

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.util.PostPopularityUtils
import com.gp.socialapp.util.DateUtils
import com.gp.socialapp.utils.State
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class FeedPostViewModel @Inject constructor(
    val repository: PostRepository,
    val replyRepository: ReplyRepository,
    val userRepository: UserRepository
) : ViewModel() {
    private val _tags = mutableSetOf<String>()
    val tags: Set<String> = _tags
    private val _selectedTagFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedTagFilters = _selectedTagFilters.asStateFlow()
    init {
        getAllPosts()
    }

    private val _isSortedByNewest = MutableStateFlow(true)
    val isSortedByNewest = _isSortedByNewest.asStateFlow()
    private val _uiState = MutableStateFlow<State<List<Post>>>(State.Idle)
    val uiState = _uiState.asStateFlow()

    private fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = State.Loading
            repository.getAllLocalPosts().collect { posts ->
                posts.forEach { post ->
                    _tags.addAll(post.tags.map{it.label})
                }
                val filteredPosts = if (selectedTagFilters.value.isNotEmpty()) {
                    posts.filter { post ->
                        post.tags.map { it.label }.intersect(selectedTagFilters.value).isNotEmpty()
                    }
                } else {
                    posts
                }
                val sortedPosts = if (isSortedByNewest.value) {
                    filteredPosts.sortedByDescending { DateUtils.convertStringToDate(it.publishedAt) }
                } else {
                    filteredPosts.sortedByDescending { PostPopularityUtils.calculateInteractionValue(it.votes, it.replyCount) }
                }
                withContext(Dispatchers.Main) {
                    _uiState.value = State.SuccessWithData(sortedPosts)
                    Log.d("TAG258", "New Data: $sortedPosts")
                }
            }
        }
    }

    fun upVote(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upVotePost(post)
        }
    }

    fun downVote(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.downVotePost(post)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }

    fun deletePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePost(post)
            repository.deleteLocalPost(post)
        }
    }

    fun sortPostsByNewest() {
//        viewModelScope.launch {
//            _isSortedByNewest.value = true
//            val posts = (uiState.value as? State.SuccessWithData)?.data ?: return@launch
//            withContext(Dispatchers.Default) {
//                Collections.sort(posts, Post.sortByDate)
//            }
//            withContext(Dispatchers.Main) {
//                _uiState.value = State.Loading
//                _uiState.value = State.SuccessWithData(posts)
//            }
//            unfilteredPosts.clear()
//            unfilteredPosts.addAll(posts)
//            Log.d("TAG258", "New Data by Newest: ${(uiState.value as? State.SuccessWithData<List<Post>>)?.data?.map{"${it.title} : ${it.votes}"} ?: emptyList()}")
//        }
        _isSortedByNewest.value = true
        getAllPosts()
    }

    fun sortPostsByPopularity() {
//        viewModelScope.launch {
//            _isSortedByNewest.value = false
//            val posts = (uiState.value as? State.SuccessWithData)?.data ?: return@launch
//
//            withContext(Dispatchers.Default) {
//                Collections.sort(posts, Post.sortByVotes)
//            }
//            withContext(Dispatchers.Main) {
//                _uiState.value = State.Loading
//                _uiState.value = State.SuccessWithData(posts)
//            }
//            unfilteredPosts.clear()
//            unfilteredPosts.addAll(posts)
//            Log.d("TAG258", "New Data by most popular: ${(uiState.value as? State.SuccessWithData<List<Post>>)?.data?.map{"${it.title} : ${it.votes}"} ?: emptyList()}")
//        }
        _isSortedByNewest.value = false
        getAllPosts()
    }
    fun updateTagFilters(newFilters: List<String>){
        _selectedTagFilters.value = newFilters.toSet()
        getAllPosts()
    }
}