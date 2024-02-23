package com.gp.chat.presentation.groupdetails

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.chat.R
import com.gp.chat.presentation.createGroupChat.GroupAvatarSection
import com.gp.chat.presentation.createGroupChat.GroupMemberItem
import com.gp.chat.presentation.theme.AppTheme
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
    GroupDetailsScreen(
        modifier = modifier,
        avatarURL = avatarURL,
        isAdmin = isAdmin,
        onChangeAvatarClicked = onChangeAvatarClicked,
        name = name,
        onChangeName = { viewModel.updateGroupName(it) },
        members = members,
        admins = admins,
        onAddMembersClicked = onAddMembersClicked,
        onUserClicked = {
            clickedUser = it
            isUserClickedDialogOpen = true
        },
        isRemoveMemberDialogOpen = isRemoveMemberDialogOpen,
        onDismissRemoveMembersDialog = { isRemoveMemberDialogOpen = false },
        onConfirmMemberRemoval = {
            onRemoveMember(clickedUser)
            isRemoveMemberDialogOpen = false
        },
        clickedUser = clickedUser,
        isUserClickedDialogOpen = isUserClickedDialogOpen,
        isCurrentUser = clickedUser.email == viewModel.currentUserEmail,
        onRemoveMemberClicked = {
            isRemoveMemberDialogOpen = true
            isUserClickedDialogOpen = false
        },
        onMessageUser = onMessageUser,
        onDismissUserClickedDialog = { isUserClickedDialogOpen = false },
        onViewProfile = onViewProfile
    )
}

@Composable
fun GroupDetailsScreen(
    modifier: Modifier = Modifier,
    avatarURL: String,
    isAdmin: Boolean,
    onChangeAvatarClicked: () -> Unit,
    name: String,
    onChangeName: (String) -> Unit,
    members: List<User>,
    admins: List<String>,
    onAddMembersClicked: () -> Unit,
    onUserClicked: (User) -> Unit,
    isRemoveMemberDialogOpen: Boolean,
    onDismissRemoveMembersDialog: () -> Unit,
    onConfirmMemberRemoval: () -> Unit,
    clickedUser: User,
    isUserClickedDialogOpen: Boolean,
    isCurrentUser: Boolean,
    onRemoveMemberClicked: () -> Unit,
    onMessageUser: (User) -> Unit,
    onDismissUserClickedDialog: () -> Unit,
    onViewProfile: (User) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
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
                onChangeName = onChangeName
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
        if (isRemoveMemberDialogOpen) {
            RemoveMemberAlertDialog(
                onDismissRequest = onDismissRemoveMembersDialog,
                onConfirmation = onConfirmMemberRemoval,
                dialogTitle = "Are you sure you want to remove ${clickedUser.firstName} ${clickedUser.lastName} from this group?"
            )
        }
        if (isUserClickedDialogOpen) {
            UserClickedDialog(
                isAdmin = isAdmin,
                isCurrentUser = isCurrentUser,
                user = clickedUser,
                onRemoveMember = onRemoveMemberClicked,
                onMessageUser = {
                    onMessageUser(it)
                    onDismissUserClickedDialog()
                },
                onViewProfile = {
                    onViewProfile(it)
                    onDismissUserClickedDialog()
                },
                onDismiss = { onDismissUserClickedDialog() }
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
        Icon(
            painterResource(id = R.drawable.add_people_circle),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .size(55.dp)
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
    onRemoveMember: () -> Unit,
    onMessageUser: (User) -> Unit,
    onViewProfile: (User) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 225.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
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
                if (isAdmin && !isCurrentUser) {
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
                                    onRemoveMember()
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

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GroupDetailsPreview() {
    val members = listOf<User>(
        User(
            firstName = "Marshall",
            lastName = "Bonner",
            profilePictureURL = "",
            phoneNumber = "(773) 502-1779",
            email = "humberto.howe@example.com",
            bio = "Life is roblox",
        ),
        User(
            firstName = "Neil",
            lastName = "Mercer",
            profilePictureURL = "",
            phoneNumber = "(761) 954-8085",
            email = "kristie.ayers@example.com",
            bio = "Bring out the lobster",
        ),
        User(
            firstName = "Phoebe",
            lastName = "Barnes",
            profilePictureURL = "",
            phoneNumber = "(644) 812-8554",
            email = "lillian.mcmahon@example.com",
            bio = "They didn't believe in us ...",
        ),
        User(
            firstName = "Beverley",
            lastName = "Sheppard",
            profilePictureURL = "",
            phoneNumber = "(267) 216-7670",
            email = "leonard.mills@example.com",
            bio = "God did!",
        )
    )
    val admins = listOf<String>(
        "leonard.mills@example.com",
        "kristie.ayers@example.com",
    ).map { RemoveSpecialChar.removeSpecialCharacters(it) }
    AppTheme {
        GroupDetailsScreen(
            avatarURL = "",
            isAdmin = true,
            onChangeAvatarClicked = { /*TODO*/ },
            name = "Testawya",
            onChangeName = {},
            members = members,
            admins = admins,
            onAddMembersClicked = { /*TODO*/ },
            onUserClicked = {},
            isRemoveMemberDialogOpen = false,
            onDismissRemoveMembersDialog = { /*TODO*/ },
            onConfirmMemberRemoval = { /*TODO*/ },
            clickedUser = User(),
            isUserClickedDialogOpen = false,
            isCurrentUser = false,
            onRemoveMemberClicked = { /*TODO*/ },
            onMessageUser = {},
            onDismissUserClickedDialog = { /*TODO*/ },
            onViewProfile = {}
        )
    }
}
