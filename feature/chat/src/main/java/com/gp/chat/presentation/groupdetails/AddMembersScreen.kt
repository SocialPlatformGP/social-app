package com.gp.chat.presentation.groupdetails

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.chat.presentation.createGroupChat.ChooseGroupMembersSection
import com.gp.chat.presentation.groupdetails.addGroupMembers.AddMembersViewModel
import com.gp.chat.presentation.theme.AppTheme
import com.gp.users.model.SelectableUser
import com.gp.users.model.User

@Composable
fun AddMembersScreen(
    viewModel: AddMembersViewModel,
    onAddMembersClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedUsers by viewModel.selectedUsers.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    AddMembersScreen(
        modifier = modifier,
        selectedUsers = selectedUsers,
        users = users,
        onRemoveMember = viewModel::removeMember,
        onAddMember = viewModel::addMember,
        onAddMembersClicked = onAddMembersClicked
    )
}

@Composable
fun AddMembersScreen(
    modifier: Modifier = Modifier,
    selectedUsers: List<User>,
    users: List<SelectableUser>,
    onRemoveMember: (User) -> Unit,
    onAddMember: (User) -> Unit,
    onAddMembersClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        ChooseGroupMembersSection(
            modifier = Modifier.padding(top = 8.dp),
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
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = onAddMembersClicked,
            enabled = selectedUsers.isNotEmpty(),
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Add Selected Members",
                style = MaterialTheme.typography.labelLarge,
            )
        }
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
fun AddMembersScreenPreview() {
    val selectedUsers = listOf<User>(
        User(
            firstName = "Marshall",
            lastName = "Bonner",
            profilePictureURL = "",
            phoneNumber = "(773) 502-1779",
            email = "humberto.howe@example.com",
            bio = "Life is roblox",
        ),
        User(
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
        AddMembersScreen(
//            selectedUsers = selectedUsers,
            selectedUsers = emptyList(),
            users = users,
            onRemoveMember = {},
            onAddMember = {},
            onAddMembersClicked = {}
        )
    }
}