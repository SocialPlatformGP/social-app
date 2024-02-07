package com.gp.posts.presentation.postsSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gp.posts.R

@Composable
fun SuggestScreen(
    viewModel: SearchViewModel, searchResultsClicked:()-> Unit
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
                    // Search Text Field with Icon
                    TextField(
                        value = searchText,
                        onValueChange = { newText ->
                            searchText = newText
                            viewModel.searchPostsByTitle(newText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        placeholder = { Text("Search") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
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
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }
        }

        items(state) { item ->
//            SuggestItem(
//                post = item,
//                searchText = searchText
//            )
        }
    }
}

@Composable
fun SuggestItem(post: SuggestedItem, searchText: String) {
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
            // Display Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.imageUrl)
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
                        text = post.name,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "4d"//DateUtils.calculateTimeDifference(post.time)
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

data class SuggestedItem(
    val name: String,
    val title: String,
    val time: String,
    val imageUrl: String
)


val suggestedItems = listOf(
    SuggestedItem("John Doe", "Software Engineer", "2h", "https://example.com/john_doe.jpg"),
    SuggestedItem("Jane Smith", "UX Designer", "1d", "https://example.com/jane_smith.jpg"),

)

@Preview(showBackground = true)
@Composable
fun SuggestItemPreview() {

     SuggestItem(post = suggestedItems.first(), searchText = "SearchQuery")
}
