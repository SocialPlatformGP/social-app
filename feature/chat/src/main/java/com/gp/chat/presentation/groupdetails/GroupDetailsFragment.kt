package com.gp.chat.presentation.groupdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gp.chat.listener.OnGroupMemberClicked
import com.gp.chat.presentation.groupdetails.addGroupMembers.AddMembersDialogFragment
import com.gp.users.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailsFragment : Fragment(), OnGroupMemberClicked {
    private val viewModel: GroupDetailsViewModel by viewModels()
    private val args: GroupDetailsFragmentArgs by navArgs()
    private lateinit var composeView: ComposeView
    private val isAdmin = true
    private val galleryImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        TODO("upload new picture and delete old one")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUsersList(args.groupId)
        composeView.setContent {
            MaterialTheme {
                GroupDetailsScreen(viewModel = viewModel,
                    isAdmin = isAdmin,
                    onChangeAvatarClicked = { onEditPictureClick() },
                    onAddMembersClicked = { onAddMembersClicked() },
                    onUserClicked = { onMemberClicked(it) })
            }
        }
    }


    private fun onAddGroupMember(user: User) {
        TODO("Check if the function in firebase client is working or not if not fix")
    }

    private fun onRemoveGroupMember(user: User) {
        viewModel.removeGroupMember(args.groupId, user)
    }

    fun onEditPictureClick() {
        val items = arrayOf("Take Photo", "Choose Existing Photo")
        MaterialAlertDialogBuilder(requireContext()).setItems(items) { dialog, which ->
            when (which) {
                0 -> {
                    onTakePhotoSelected()
                }

                1 -> {
                    onChoosePhotoSelected()
                }
            }
        }.show()
    }

    private fun onTakePhotoSelected() {
        TODO("implement camera capturing")
    }

    private fun onChoosePhotoSelected() {
        galleryImageResultLauncher.launch("image/*")
    }

    override fun onMemberClicked(user: User) {
        val items = if (isAdmin) {
            arrayOf("View Profile", "Message", "Remove from Group")
        } else {
            arrayOf("View Profile", "Message")
        }
        MaterialAlertDialogBuilder(requireContext()).setItems(items) { dialog, which ->
            when (items[which]) {
                "View Profile" -> {
                    //TODO("Navigate to profile")
                }

                "Message" -> {
                    TODO("Navigate to dms")
                }

                "Remove from Group" -> {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Are you sure you want to remove ${user.firstName + user.lastName}?")
                        .setPositiveButton("Remove") { dialog, which ->
                            onRemoveGroupMember(user)
                        }.setNegativeButton("Cancel") { _, _ ->
                        }.show()
                }
            }
        }.show()
    }

    fun onAddMembersClicked() {
        val dialogFragment = AddMembersDialogFragment()
        val bundle = Bundle()
        bundle.putParcelableArrayList("users", ArrayList(viewModel.users.value))
        bundle.putString("group_id", args.groupId)
        dialogFragment.arguments = bundle
        dialogFragment.show(childFragmentManager, "AddMembersDialogFragment")
    }
}