package com.gp.posts.presentation.postsSearch
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.gp.posts.presentation.postsfeed.FeedPostItem
import com.gp.socialapp.model.Post


@Composable
fun SearchResultScreen(viewModel: SearchResultsViewModel,searchQuery:String,isTag:Boolean) {
    if(isTag){
        viewModel.searchPostsByTag(searchQuery)
    }
    else{
        viewModel.searchPostsByTitle(searchQuery)
        Log.d("details", "SearchResultScreen: $searchQuery")
        Log.d("details", "SearchResultScreen:$isTag ")
    }
    val state by viewModel.searchResult.collectAsState()
    SearchResultContent(
        state = state,
        deletePost ={viewModel.deletePost(it)} ,
        details = { },
        edit = { },
        onUpVote = {viewModel.upVote(it)},
        onDownVote = {viewModel.downVote(it)}
    )


}

@Composable
fun SearchResultContent(
    state: List<Post>,
    deletePost: (Post) -> Unit,
    details: (Post) -> Unit,
    edit: (Post) -> Unit,
    onUpVote: (Post) -> Unit,
    onDownVote: (Post) -> Unit,
) {

    LazyColumn( modifier = Modifier.fillMaxWidth(1f)){
        items(state){
            FeedPostItem(
                post = it,
                postEvent = {/*TODO*/},
            )

        }

    }


}
