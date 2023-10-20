package com.gp.posts.presentation.postsSearch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {

    var searchResult :MutableStateFlow<List<PostEntity>> = MutableStateFlow(listOf())

    fun searchPosts(text: String) {
        viewModelScope.launch (Dispatchers.IO){
            repository.searchPostsByTitle(text).collect{
                searchResult.value = it
            }
        }

    }
    }
