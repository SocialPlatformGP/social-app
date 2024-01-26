package com.gp.posts.presentation.postsSearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gp.posts.presentation.postsSearch.SearchResultsViewModel
import com.gp.socialapp.model.Post

@Composable
fun suggestScreen(viewModel:SearchResultsViewModel,showDetails:(String)->Unit){
    val searchResult by viewModel.searchResult.collectAsState(initial = emptyList())

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)) {
        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(searchResult.size){
                val post = searchResult[it]
                suggestItem(title = post.title,showDetails)

            }
        }

    }
}
@Composable
fun suggestItem(title:String,showDetails:(String)->Unit){
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp).clickable{showDetails(title)},
        elevation = 4.dp
        , shape = MaterialTheme.shapes.medium
    ) {
        Column (modifier = Modifier.fillMaxSize().padding(4.dp)){
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )

        }

    }
}