package com.gp.chat.presentation.groupdetails

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.chat.R
import com.gp.chat.presentation.createGroupChat.GroupAvatarSection
import com.gp.chat.presentation.createGroupChat.GroupMemberItem
import com.gp.chat.util.RemoveSpecialChar
import com.gp.users.model.SelectableUser
import com.gp.users.model.User

@Composable
fun GroupDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupDetailsViewModel,
    isAdmin: Boolean = false,
    onChangeAvatarClicked: () -> Unit,
    onAddMembersClicked: () -> Unit,
    onUserClicked: (User) -> Unit,
) {
    val name by viewModel.groupName.collectAsStateWithLifecycle()
    val avatarURL by viewModel.avatarURL.collectAsStateWithLifecycle()
    val admins by viewModel.admins.collectAsStateWithLifecycle()
    val members by viewModel.users.collectAsStateWithLifecycle()
    Log.d("seerde", "Name: $name")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GroupAvatarSection(
            avatarURL = avatarURL,
            isModifiable = isAdmin,
            onChoosePhotoClicked = onChangeAvatarClicked
        )
        GroupNameSection(
            name = name,
            isModifiable = isAdmin,
            onChangeName = viewModel::updateGroupName
        )
        Divider(modifier = Modifier.height(2.dp))
        GroupMembersSection(
            members = members,
            admins = admins,
            isAdmin = isAdmin,
            onAddMembersClicked = onAddMembersClicked,
            onUserClicked = onUserClicked
        )
    }
}

@Composable
fun GroupNameSection(
    name: String,
    isModifiable: Boolean,
    onChangeName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isModifying by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(name) {
        newName = name
    }
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        if (isModifying) {
            OutlinedTextField(
                value = newName,
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Group Name is Required",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange = {
                    isError = it.isBlank()
                    newName = it
                },
                label = {
                    Text(text = "Group Name")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        isModifying = !isModifying
                        onChangeName(newName)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .focusRequester(focusRequester)
            )
            LaunchedEffect(true) {
                focusRequester.requestFocus()
            }
        } else {
            Text(
                text = newName,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (isModifiable && !isModifying) {
            IconButton(
                onClick = {
                    isModifying = !isModifying
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Create,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun GroupMembersSection(
    members: List<User>,
    admins: List<String>,
    isAdmin: Boolean = false,
    onAddMembersClicked: () -> Unit,
    onUserClicked: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.d("SEERDE", "GroupMembersSection:  members: ${members.map { it.email }}")
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Members:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${members.size} Member${if (members.size != 1) "s" else ""}",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(if (isAdmin) listOf(User()) + members else members) { user ->
                if (user.email.isBlank()) {
                    AddMembersSection(onAddMembersClicked)
                } else {
                    GroupMemberItem(
                        user = SelectableUser(user, false),
                        isAdmin = admins.any { RemoveSpecialChar.removeSpecialCharacters(user.email) == it },
                        onUserClick = {
                            onUserClicked(it)
                        })
                }
            }
        }
    }
}

@Composable
fun AddMembersSection(
    onAddMembersClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth()
            .clickable {
                onAddMembersClicked()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_add_user),
            contentDescription = null,
            contentScale = ContentScale.None,
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = modifier.width(12.dp))
        Text(
            text = "Add Members",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 20.sp
        )
    }
}
