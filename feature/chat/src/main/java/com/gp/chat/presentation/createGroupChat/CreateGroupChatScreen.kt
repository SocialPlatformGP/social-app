package com.gp.chat.presentation.createGroupChat

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.gp.chat.R
import com.gp.chat.presentation.theme.AppTheme
import com.gp.chat.utils.CircularAvatar
import com.gp.users.model.SelectableUser
import com.gp.users.model.User

@Composable
fun CreateGroupChatScreen(
    viewModel: CreateGroupChatViewModel,
    onChoosePhotoClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val avatarURL by viewModel.avatarURL.collectAsStateWithLifecycle()
    val selectedUsers by viewModel.selectedUsers.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    var isError by rememberSaveable { mutableStateOf(false) }
    CreateGroupChatScreen(
        modifier = modifier,
        name = name,
        avatarURL = avatarURL,
        selectedUsers = selectedUsers,
        users = users,
        isError = isError,
        onChoosePhotoClicked = onChoosePhotoClicked,
        onChangeError = { isError = it },
        onUpdateName = { viewModel.updateName(it) },
        onRemoveMember = { viewModel.removeMember(it) },
        onAddMember = { viewModel.addMember(it) },
        onCreateGroup = { viewModel.createGroup() }
    )
}

@Composable
fun CreateGroupChatScreen(
    modifier: Modifier = Modifier,
    name: String,
    avatarURL: String,
    selectedUsers: List<User>,
    users: List<SelectableUser>,
    isError: Boolean,
    onChoosePhotoClicked: () -> Unit,
    onChangeError: (Boolean) -> Unit,
    onUpdateName: (String) -> Unit,
    onRemoveMember: (User) -> Unit,
    onAddMember: (User) -> Unit,
    onCreateGroup: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
        ) {
            GroupAvatarSection(
                avatarURL = avatarURL,
                isModifiable = true,
                onChoosePhotoClicked = onChoosePhotoClicked
            )
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.OutlinedTextField(
                shape = RoundedCornerShape(32.dp),
                value = name,
                isError = isError,
                supportingText = {
                    if (isError) {
                        androidx.compose.material3.Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Group Name is Required",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange = {
                    onChangeError(false)
                    onUpdateName(it)
                },
                label = {
                    androidx.compose.material3.Text(
                        text = "Group Name",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)

                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
            ChooseGroupMembersSection(
                selectedUsers = selectedUsers,
                users = users,
                onUnselectUser = {
                    onRemoveMember(it)
                },
                onUserClick = { user ->
                    if (user.isSelected) {
                        onAddMember(user.data)
                    } else {
                        onRemoveMember(user.data)
                    }
                })
        }
        androidx.compose.material3.Button(
            onClick = {
                onChangeError(name.isBlank())
                if (!isError) {
                    onCreateGroup()
                }
            },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(54.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            androidx.compose.material3.Text(
                text = "Create Group",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
//                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun GroupAvatarSection(
    avatarURL: String,
    isModifiable: Boolean = false,
    onChoosePhotoClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = contentColorFor(backgroundColor)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (avatarURL.isBlank()) {
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.ic_group),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(
                    model = Uri.parse(avatarURL)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }
        if (isModifiable) {
            androidx.compose.material3.IconButton(
                onClick = { onChoosePhotoClicked() },
                modifier = Modifier
                    .offset(x = 38.dp, y = 38.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .size(32.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Create,
                    contentDescription = null,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseGroupMembersSection(
    modifier: Modifier = Modifier,
    selectedUsers: List<User>,
    onUnselectUser: (User) -> Unit,
    users: List<SelectableUser>,
    onUserClick: (SelectableUser) -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        FlowRow {
            selectedUsers.forEach { user ->
                SelectedMemberItem(
                    user = user,
                    onUnselect = onUnselectUser
                )
            }
        }
        androidx.compose.material3.Divider(
            thickness = 1.dp, modifier = modifier.padding(top = 16.dp)
        )
        LazyColumn {
            items(users) { user ->
                GroupMemberItem(
                    user = user,
                    isSelected = user.isSelected,
                    isSelectable = true,
                    onUserClick = {
                        onUserClick(user.copy(isSelected = !user.isSelected))
                    })
            }
        }
    }
}

@Composable
fun SelectedMemberItem(
    user: User,
    onUnselect: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = contentColorFor(backgroundColor)
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularAvatar(
                imageURL = user.profilePictureURL,
                size = 50.dp,
                placeHolderImageVector = Icons.Filled.AccountCircle
            )
            androidx.compose.material3.IconButton(
                onClick = { onUnselect(user) },
                modifier = Modifier
                    .offset(x = 18.dp, y = 18.dp)
                    .background(color = backgroundColor, shape = CircleShape)
                    .size(20.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
        androidx.compose.material3.Text(
            text = user.firstName,
            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun GroupMemberItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isSelectable: Boolean = false,
    user: SelectableUser,
    isAdmin: Boolean = false,
    onUserClick: (User) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth()
            .clickable {
                onUserClick(user.data)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularAvatar(
            imageURL = user.data.profilePictureURL,
            size = 55.dp,
            placeHolderImageVector = Icons.Filled.AccountCircle
        )
        Spacer(modifier = modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Column {
                androidx.compose.material3.Text(
                    text = user.data.firstName + " " + user.data.lastName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 20.sp,
                )
                if (user.data.bio.isNotBlank()) {
                    androidx.compose.material3.Text(
                        text = user.data.bio,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isSelectable) {
                CircleCheckbox(
                    selected = isSelected,
                    onChecked = { onUserClick(user.data) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            } else if (isAdmin) {
                Image(
                    painter = painterResource(id = R.drawable.ic_admin_24),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun CircleCheckbox(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    onChecked: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val imageVector =
        if (selected) Icons.Filled.CheckCircle else ImageVector.vectorResource(R.drawable.circle_outline)
//    val tint = if (selected) colors.primary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f)
    val background = if (selected) colors.primaryContainer else Color.Transparent

    androidx.compose.material3.IconButton(
        onClick = { onChecked() },
        modifier = modifier.size(24.dp),
        enabled = enabled
    ) {
        androidx.compose.material3.Icon(
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.background(background, shape = CircleShape),
            contentDescription = "checkbox"
        )
    }
}

@Preview(name = "Light", showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun CreateGroupChatScreenPreview() {
    val selectedUsers = listOf<User>(
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
    val users = listOf<SelectableUser>(
        SelectableUser(
            data = User(
                firstName = "Marshall",
                lastName = "Bonner",
                profilePictureURL = "",
                phoneNumber = "(773) 502-1779",
                email = "humberto.howe@example.com",
                bio = "Life is roblox",
            ),
            isSelected = true
        ),
        SelectableUser(
            data = User(
                firstName = "Neil",
                lastName = "Mercer",
                profilePictureURL = "",
                phoneNumber = "(761) 954-8085",
                email = "kristie.ayers@example.com",
                bio = "Bring out the lobster",
            ),
            isSelected = false
        ),
        SelectableUser(
            data = User(
                firstName = "Phoebe",
                lastName = "Barnes",
                profilePictureURL = "",
                phoneNumber = "(644) 812-8554",
                email = "lillian.mcmahon@example.com",
                bio = "They didn't believe in us ...",
            ),
            isSelected = true
        ),
        SelectableUser(
            data = User(
                firstName = "Beverley",
                lastName = "Sheppard",
                profilePictureURL = "",
                phoneNumber = "(267) 216-7670",
                email = "leonard.mills@example.com",
                bio = "God did!",
            ),
            isSelected = false
        )
    )
    AppTheme {
        Surface {
            CreateGroupChatScreen(
                name = "",
                avatarURL = "",
                selectedUsers = selectedUsers,
                users = users,
                isError = false,
                onChoosePhotoClicked = { /*TODO*/ },
                onChangeError = {},
                onUpdateName = {},
                onRemoveMember = {},
                onAddMember = {},
                onCreateGroup = {}
            )
        }
    }
}
