package com.gp.chat.presentation.groupdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.presentation.theme.AppTheme
import com.gp.socialapp.utils.State
import com.gp.users.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupDetailsFragment : Fragment() {
    private val viewModel: GroupDetailsViewModel by viewModels()
    private val args: GroupDetailsFragmentArgs by navArgs()
    private lateinit var composeView: ComposeView
    private val galleryImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
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

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUsersList(args.groupId)
        composeView.setContent {
            AppTheme {
                GroupDetailsScreen(
                    viewModel = viewModel,
                    isAdmin = args.isAdmin,
                    onChangeAvatarClicked = {
                        galleryImageResultLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    },
                    onAddMembersClicked = { onAddMembersClicked() },
                    onViewProfile = {/*TODO navigate to user profile*/ },
                    onMessageUser = { onMessageUser(it) },
                    onRemoveMember = { onRemoveGroupMember(it) },
                )
            }
        }
    }

    private fun onRemoveGroupMember(user: User) {
        viewModel.removeGroupMember(args.groupId, user)
    }

    private fun onMessageUser(user: User) {
        Log.d("SEERDE", "onMessageUser: onMessageUser called")
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.haveChatWithUserState.flowWithLifecycle(lifecycle).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        if (it.data != "-1") {
                            Log.d("SEERDE", "onMessageUser: received success response")
                            val currentSender = Firebase.auth.currentUser
                            val action =
                                GroupDetailsFragmentDirections.actionGroupDetailsFragmentToPrivateChatFragment(
                                    chatId = it.data,
                                    receiverName = user.firstName + " " + user.lastName,
                                    receiverPic = user.profilePictureURL,
                                    senderPic = currentSender?.photoUrl.toString(),
                                    senderName = currentSender?.displayName!!
                                )
                            findNavController().navigate(action)
//                            TODO("Fix app crash when it navigates to private chat")
                        } else {
                            viewModel.createNewChat(user.email)
                        }
                    }

                    is State.Error -> {
                        Log.e("SEERDE", "onMessageUser: ${it.message}")
                    }

                    else -> {}
                }
            }
        }
        viewModel.messageUser(user.email)
    }

    fun onAddMembersClicked() {
        val action = GroupDetailsFragmentDirections.actionGroupDetailsFragmentToAddMembersFragment(
            viewModel.users.value.toTypedArray(),
            args.groupId
        )
        findNavController().navigate(action)
    }
}