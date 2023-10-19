package com.gp.posts.presentation.postsSearch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {

    val searchQuery= MutableStateFlow("")
    val postsFlow=searchQuery.flatMapLatest {
        repository.searchPostsByTitle(it)
    }
       val queryPosts= postsFlow.asLiveData()
}