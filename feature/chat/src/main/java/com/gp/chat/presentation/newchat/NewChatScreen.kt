package com.gp.chat.presentation.newchat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.chat.utils.CircularAvatar
import com.gp.users.model.User

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewChatScreen(
    viewModel: NewChatViewModel,
    onUserClick: (User) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    NewChatScreen(
        searchText = searchText,
        onSearchTextChange = viewModel::onSearchTextChange,
        users = users,
        onUserClick = onUserClick,
//        keyboardController = LocalSoftwareKeyboardController.current,
        onBackPressed = onBackPressed,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NewChatScreen(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    users: List<User>,
    onUserClick: (User) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
//                val keyboardController = LocalSoftwareKeyboardController.current
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "Navigate Back"
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                SearchBar(
                    query = searchText,
                    onQueryChange = onSearchTextChange,
                    onSearch = {  },
                    active = false,
                    placeholder = {
                        Text(text = "Search Users")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { onSearchTextChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    content = {},
                    onActiveChange = {},
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        },
        content = { paddingValues ->
            NewChatContent(users, onUserClick, Modifier.padding(paddingValues))
        }
    )
}

@Composable
fun NewChatContent(
    users: List<User>,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(users) { user ->
            UserItem(user = user, onUserClick = onUserClick)
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth()
            .clickable {
                onUserClick(user)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularAvatar(
            imageURL = user.profilePictureURL,
            size = 55.dp
        )
        Spacer(modifier = modifier.width(12.dp))
        Text(
            text = user.firstName + " " + user.lastName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 20.sp
        )
    }
}
