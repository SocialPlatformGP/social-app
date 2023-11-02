package com.gp.posts.presentation.postsSearch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val postRepo: PostRepository, private val userRepo: UserRepository) : ViewModel() {
    var searchResult :MutableStateFlow<List<Post>> = MutableStateFlow(listOf())
    fun searchPosts(text: String) {
        viewModelScope.launch (Dispatchers.IO){
            postRepo.searchPostsByTitle(text).collect{
                searchResult.value = it
            }
        }
    }
}
