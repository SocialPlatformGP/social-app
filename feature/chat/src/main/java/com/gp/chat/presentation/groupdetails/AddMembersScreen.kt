package com.gp.chat.presentation.groupdetails

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gp.chat.presentation.createGroupChat.ChooseGroupMembersSection
import com.gp.chat.presentation.groupdetails.addGroupMembers.AddMembersViewModel

@Composable
fun AddMembersScreen(
    viewModel: AddMembersViewModel,
    onAddMembersClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedUsers by viewModel.selectedUsers.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ChooseGroupMembersSection(
            selectedUsers = selectedUsers,
            users = users,
            onUnselectUser = {
                viewModel.removeMember(it)
            },
            onUserClick = {user ->
                if(user.isSelected) {
                    viewModel.addMember(user.data)
                } else {
                    viewModel.removeMember(user.data)
                }
            })
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = onAddMembersClicked,
            enabled = selectedUsers.isNotEmpty(),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "Add Selected Members")
        }

    }
}