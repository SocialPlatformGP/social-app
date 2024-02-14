package com.gp.chat.presentation.groupdetails

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.integrity.internal.t
import com.gp.chat.R
import com.gp.chat.presentation.createGroupChat.GroupAvatarSection
import com.gp.chat.presentation.createGroupChat.GroupMemberItem
import com.gp.chat.util.RemoveSpecialChar
import com.gp.users.model.SelectableUser
import com.gp.users.model.User
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun GroupDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupDetailsViewModel,
    isAdmin: Boolean = false,
    onChangeAvatarClicked: () -> Unit,
    onAddMembersClicked: () -> Unit,
    onViewProfile: (User) -> Unit,
    onMessageUser: (User) -> Unit,
    onRemoveMember: (User) -> Unit,
) {
    val name by viewModel.groupName.collectAsStateWithLifecycle()
    val avatarURL by viewModel.avatarURL.collectAsStateWithLifecycle()
    val admins by viewModel.admins.collectAsStateWithLifecycle()
    val members by viewModel.users.collectAsStateWithLifecycle()
    var clickedUser by rememberSaveable {
        mutableStateOf(User())
    }
    var isUserClickedDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isRemoveMemberDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    Log.d("seerde", "Name: $name")
    Box(

    ) {
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
                onUserClicked = {
                    clickedUser = it
                    isUserClickedDialogOpen = true
                }
            )
        }
        if(isRemoveMemberDialogOpen){
            RemoveMemberAlertDialog(
                onDismissRequest = { isRemoveMemberDialogOpen = false },
                onConfirmation = {
                    onRemoveMember(clickedUser)
                    isRemoveMemberDialogOpen = false
                },
                dialogTitle = "Are you sure you want to remove ${clickedUser.firstName} ${clickedUser.lastName} from this group?"
            )
        }
        if (isUserClickedDialogOpen) {
            UserClickedDialog(
                isAdmin = isAdmin,
                isCurrentUser = clickedUser.email == viewModel.currentUserEmail,
                user = clickedUser,
                onRemoveMember = {
                    isRemoveMemberDialogOpen = true
                    isUserClickedDialogOpen = false
                },
                onMessageUser = {
                    onMessageUser(it)
                    isUserClickedDialogOpen = false
                },
                onViewProfile = {
                    onViewProfile(it)
                    isUserClickedDialogOpen = false
                },
                onDismiss = { isUserClickedDialogOpen = false}
            )
        }
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

@Composable
fun UserClickedDialog(
    modifier: Modifier = Modifier,
    isAdmin: Boolean,
    isCurrentUser: Boolean,
    user: User,
    onDismiss: () -> Unit,
    onRemoveMember: (User) -> Unit,
    onMessageUser: (User) -> Unit,
    onViewProfile: (User) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card (
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 225.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ){
            LazyColumn (
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                item {
                    Text(
                        text = "View Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .weight(1F)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxSize()
                            .clickable {
                                onViewProfile(user)
                            })
                }
                item {
                    Text(text = "Message",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .weight(1F)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxSize()
                            .clickable {
                                onMessageUser(user)
                            })
                }
                if(isAdmin && !isCurrentUser) {
                    item {
                        Text(
                            text = "Remove from Group",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1F)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxSize()
                                .clickable {
                                    onRemoveMember(user)
                                })
                    }
                }
            }
        }
    }

}

@Composable
fun RemoveMemberAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}


@Preview
@Composable
fun UserClickedDialogPreview() {
    MaterialTheme {
        UserClickedDialog(
            isAdmin = true,
            isCurrentUser = false,
            user = User(),
            onRemoveMember = {},
            onMessageUser = {},
            onDismiss = {},
            onViewProfile = {}
        )
    }
}
