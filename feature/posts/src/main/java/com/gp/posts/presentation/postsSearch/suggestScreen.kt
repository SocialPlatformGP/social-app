package com.gp.posts.presentation.postsSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.R
import com.gp.socialapp.model.Post
import com.gp.socialapp.util.DateUtils
@Composable
fun SuggestScreen(
    viewModel: SearchViewModel,
    searchResultsClicked: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val state by viewModel.searchResult.collectAsState()

    LazyColumn {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = 4.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextField(
                        value = searchText,
                        onValueChange = { newText ->
                            searchText = newText
                            viewModel.searchPostsByTitle(newText)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        placeholder = { Text("Search") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface
                            )
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchText = ""
                                        viewModel.searchPostsByTitle(searchText)
                                    },
                                    modifier = Modifier.background(Color.Transparent)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            textColor = MaterialTheme.colors.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )

                    IconButton(
                        onClick = { searchResultsClicked(searchText) },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Results",
                            tint = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }

        if (state.isNotEmpty() || searchText.isEmpty()) {
            items(state) { item ->
                SuggestItem(
                    post = item,
                    searchText = searchText
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {

                    Text(
                        text = "NO RESULTS",
                        style = TextStyle(
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}



@Composable
fun SuggestItem(post: Post, searchText: String) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.userPfp)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.pngwing_com),
                contentDescription = "picture image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = DateUtils.calculateTimeDifference(post.publishedAt)
                        ,
                        style = MaterialTheme.typography.subtitle1,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = generateAnnotatedTitle(post.title, searchText),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

private fun generateAnnotatedTitle(title: String, highlightedText: String): AnnotatedString {
    val startIndex = title.indexOf(highlightedText, ignoreCase = true)
    return buildAnnotatedString {
        append(title)
        if (startIndex != -1) {
            val endIndex = startIndex + highlightedText.length
            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                ),
                start = startIndex,
                end = endIndex
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SuggestItemPreview() {

     //SuggestItem(post = suggestedItems.first(), searchText = "SearchQuery")
}
