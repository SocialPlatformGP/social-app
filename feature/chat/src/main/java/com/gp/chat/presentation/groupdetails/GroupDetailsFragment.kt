package com.gp.chat.presentation.groupdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gp.socialapp.utils.State
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.listener.OnGroupMemberClicked
import com.gp.chat.presentation.groupdetails.addGroupMembers.AddMembersDialogFragment
import com.gp.chat.presentation.newchat.NewChatFragmentDirections
import com.gp.chat.util.RemoveSpecialChar
import com.gp.users.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupDetailsFragment : Fragment(), OnGroupMemberClicked {
    private val viewModel: GroupDetailsViewModel by viewModels()
    private val args: GroupDetailsFragmentArgs by navArgs()
    private lateinit var composeView: ComposeView
    private val isAdmin = true
    private val galleryImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
       viewModel.updateAvatar(it)
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
//        TODO("Check if the function in firebase client is working or not if not fix")
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
//        TODO("implement camera capturing")
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
                    onMessageUser(user)
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

    private fun onMessageUser(user: User) {
        Log.d("SEERDE", "onMessageUser: onMessageUser called")
        lifecycleScope.launch (Dispatchers.IO){
            viewModel.haveChatWithUserState.flowWithLifecycle(lifecycle).collect {
                when (it){
                    is State.SuccessWithData -> {
                        if(it.data != "-1") {
                            Log.d("SEERDE", "onMessageUser: received success response")
                            val currentSender = viewModel.users.value.find { it.email == Firebase.auth.currentUser!!.email}!!
                            val action =
                                GroupDetailsFragmentDirections.actionGroupDetailsFragmentToPrivateChatFragment(
                                    chatId = it.data,
                                    receiverName = user.firstName + " " + user.lastName,
                                    receiverPic = user.profilePictureURL ?: "",
                                    senderPic = currentSender.profilePictureURL,
                                    senderName = currentSender.firstName + " " + currentSender.lastName
                                )
                            findNavController().navigate(action)
                            TODO("Fix app crash when it navigates to private chat")
                        } else {
                            viewModel.createNewChat(user.email)
                        }
                    }
                    is State.Error -> {
                        Log.e("SEERDE", "onMessageUser: ${it.message}", )
                    }
                    else -> {}
                }
            }
        }
        viewModel.messageUser(user.email)
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