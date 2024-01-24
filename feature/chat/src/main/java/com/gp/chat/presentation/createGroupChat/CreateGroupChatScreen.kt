package com.gp.chat.presentation.createGroupChat

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.gp.chat.R
import com.gp.chat.utils.CircularAvatar
import com.gp.users.model.SelectableUser
import com.gp.users.model.User

@Composable
fun CreateGroupChatScreen(
    viewModel: CreateGroupChatViewModel,
    onChoosePhotoClicked: () -> Unit,
    onCreateGroupClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val avatarURL by viewModel.avatarURL.collectAsStateWithLifecycle()
    val selectedUsers by viewModel.selectedUsers.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    var isError by rememberSaveable { mutableStateOf(false) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
//            .padding(8.dp)
        ) {
            GroupAvatarSection(
                avatarURL = avatarURL,
                isModifiable = true,
                onChoosePhotoClicked = onChoosePhotoClicked
            )
            OutlinedTextField(
                value = name,
                isError = isError,
                supportingText = {
                    if(isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Group Name is Required",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                onValueChange ={
                    isError = false
                    viewModel.updateName(it)
                },
                label = {
                    Text(text = "Group Name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),)
            ChooseGroupMembersSection(
                selectedUsers = selectedUsers,
                users = users,
                onUnselectUser = {
                    viewModel.removeMember(it)
                    Log.d("SEERDE", "CreateGroupChatScreen: OnUnselect $it")
                },
                onUserClick = {user ->
                    if(user.isSelected) {
                        viewModel.addMember(user.data)
                    } else {
                        viewModel.removeMember(user.data)
                        Log.d("SEERDE", "CreateGroupChatScreen: OnUnselect $user")
                    }
                })
        }
        Button(
            onClick = {
                isError = name.isBlank()
                if (!isError){
                    onCreateGroupClicked()
                }
            }
        ) {
            Text(
                text = "Create Group",
                style = MaterialTheme.typography.labelLarge)
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
    val backgroundColor = Color.LightGray
    val contentColor = contentColorFor(backgroundColor)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()) {
        Image(
            painter = if(avatarURL.isBlank()){
                painterResource(id = R.drawable.ic_group)
                } else{
                rememberAsyncImagePainter(
                    model = Uri.parse(avatarURL)
                )
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        if (isModifiable) {
            IconButton(
                onClick = {onChoosePhotoClicked()},
                modifier = Modifier
                    .offset(x = 38.dp, y = 38.dp)
                    .background(backgroundColor, CircleShape)
                    .size(32.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Create,
                    contentDescription = null,
                    tint = contentColor
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
    Column {
        FlowRow {
            selectedUsers.forEach {user ->
                SelectedMemberItem(
                    user = user,
                    onUnselect = onUnselectUser)
            }
        }
        Divider(
            thickness = 1.dp, modifier = modifier.padding(top = 16.dp)
        )
        LazyColumn{
            items(users){user ->
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
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (
            contentAlignment = Alignment.Center
        ){
            CircularAvatar(imageURL = user.profilePictureURL, size = 50.dp)
            IconButton(
                onClick = { onUnselect(user) },
                modifier = Modifier
                    .offset(x = 18.dp, y = 18.dp)
                    .background(color = Color.LightGray, shape = CircleShape)
                    .size(20.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                    contentDescription = null)
            }
        }
        Text(
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
    onUserClick: (User) -> Unit,
){
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
            size = 55.dp
        )
        Spacer(modifier = modifier.width(12.dp))
        Text(
            text = user.data.firstName + " " + user.data.lastName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 20.sp
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            if(isSelectable){
                CircleCheckbox(
                    selected = isSelected,
                    onChecked = {onUserClick(user.data)})
            }
        }
    }
}
@Composable
fun CircleCheckbox(selected: Boolean, enabled: Boolean = true, onChecked: () -> Unit) {
    val color = MaterialTheme.colorScheme
    val imageVector = if (selected) Icons.Filled.CheckCircle else ImageVector.vectorResource(R.drawable.circle_outline)
    val tint = if (selected) color.primary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f)
    val background = if (selected) Color.White else Color.Transparent

    IconButton(
        onClick = { onChecked() },
        modifier = Modifier.offset(x = 4.dp, y = 4.dp),
        enabled = enabled
    ) {
        Icon(imageVector = imageVector, tint = tint,
            modifier = Modifier.background(background, shape = CircleShape),
            contentDescription = "checkbox")
    }
}