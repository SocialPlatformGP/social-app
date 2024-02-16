package com.gp.chat.presentation.newchat

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.presentation.theme.AppTheme
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewChatFragment : Fragment(){
    private val viewModel: NewChatViewModel by viewModels()
    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            AppTheme {
                NewChatScreen(viewModel = viewModel, onUserClick = { user ->
                    onUserClicked(user.email)
                }, onBackPressed = {
                    findNavController().popBackStack()
                })
            }
        }
    }


    private fun onUserClicked(userEmail: String) {
        viewModel.createChat(userEmail)
        lifecycleScope.launch {
            viewModel.createNewChatState.flowWithLifecycle(lifecycle).collect {
                when (it) {
                    is State.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }

                    is State.SuccessWithData -> {
                        if (it.data != "-1") {
                            lifecycleScope.launch {
                                viewModel.users.flowWithLifecycle(lifecycle).collect { users ->
                                    val currentReceiver =
                                        users.find { user -> user.email == userEmail }
                                    val currentSender = Firebase.auth.currentUser
                                    val action =
                                        NewChatFragmentDirections.actionNewChatToPrivateChatFragment(
                                            chatId = it.data,
                                            receiverName = currentReceiver?.firstName + " " + currentReceiver?.lastName,
                                            receiverPic = currentReceiver?.profilePictureURL ?: "",
                                            senderPic = currentSender?.photoUrl.toString(),
                                            senderName = currentSender?.displayName!!
                                        )
                                    findNavController().navigate(action)

                                }
                            }
                        }
                    }

                    is State.Success -> {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    }

                    is State.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }
}

