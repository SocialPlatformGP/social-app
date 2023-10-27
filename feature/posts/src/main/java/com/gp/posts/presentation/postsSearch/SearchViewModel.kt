package com.gp.posts.presentation.postsSearch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.model.Post
import com.gp.socialapp.repository.PostRepository
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SearchViewModel @Inject constructor(private val postRepo: PostRepository,
private val userRepo: UserRepository) : ViewModel() {

    var searchResult :MutableStateFlow<List<Post>> = MutableStateFlow(listOf())
    val pfpURL = "https://www.shutterstock.com/image-vector/yellow-duck-toy-inflatable-rubber-260nw-1677879052.jpg"

    fun searchPosts(text: String) {
        viewModelScope.launch (Dispatchers.IO){
            //TODO("replace pfpUrl  author name and comments count with actual values")
            postRepo.searchPostsByTitle(text).collect{entityList ->
//                val newList = entityList.map{ Post(pfpURL, "Mohammed Edrees",
//                    it.publishedAt, it.title, it.body, it.upvoted, 1500) }
                searchResult.value = entityList
            }
        }

    }
    }
