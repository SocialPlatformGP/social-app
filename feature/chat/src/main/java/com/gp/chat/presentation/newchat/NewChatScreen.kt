package com.gp.chat.presentation.newchat

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.chat.presentation.theme.AppTheme
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
    Scaffold(modifier = modifier, topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(
                        top = 6.dp,
                    ),
                onClick = { onBackPressed() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
//                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Navigate Back"
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            SearchBar(
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                query = searchText,
                onQueryChange = onSearchTextChange,
                onSearch = { /*keyboardController?.hide()*/ },
                active = false,
                placeholder = {
                    Text(text = "Search Users")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
//                        tint = MaterialTheme.colorScheme.onSurface,
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    },
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
       ,
        content = { paddingValues ->
        NewChatContent(users, onUserClick, Modifier.padding(paddingValues))
    })
}

@Composable
fun NewChatContent(
    users: List<User>,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)
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
            imageURL = user.profilePictureURL, size = 55.dp
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

@Preview(name = "Light", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NewChatScreenPreview() {
    val users = listOf<User>(
        User(
            firstName = "Marshall",
            lastName = "Bonner",
            profilePictureURL = "",
            phoneNumber = "(773) 502-1779",
            email = "humberto.howe@example.com",
            bio = "Life is roblox",
        ), User(
            firstName = "Phoebe",
            lastName = "Barnes",
            profilePictureURL = "",
            phoneNumber = "(644) 812-8554",
            email = "lillian.mcmahon@example.com",
            bio = "They didn't believe in us ...",
        )
    )
    AppTheme {
        NewChatScreen(searchText = "Search Text",
            onSearchTextChange = {},
            users = users,
            onUserClick = {},
            onBackPressed = { /*TODO*/ })
    }
}
