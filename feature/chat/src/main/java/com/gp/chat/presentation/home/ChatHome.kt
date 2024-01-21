package com.gp.chat.presentation.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gp.chat.listener.OnRecentChatClicked
import com.gp.chat.model.RecentChat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatHome : Fragment(), OnRecentChatClicked {
    private val viewModel: HomeViewModel by viewModels()
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme {
                ChatHomeScreen(viewModel = viewModel, onRecentChatClicked = { recentChat ->
                    onClick(recentChat)
                }, onDropPDownItemClicked = { dropDownItem, recentChat ->
                    onDropDownItemClicked(dropDownItem, recentChat)
                }, dropDownItems = listOf(DropDownItem("Leave")), onFabClick = {
                    val action = ChatHomeDirections.actionChatHomeToNewChat()
                    findNavController().navigate(action)
                })
            }
        }
    }

    override fun onClick(recentChat: RecentChat) {
        with(recentChat) {
            val action = if (recentChat.privateChat) {
                ChatHomeDirections.actionChatHomeToPrivateChatFragment(
                    chatId = id,
                    senderName = senderName,
                    receiverName = receiverName,
                    senderPic = senderPicUrl,
                    receiverPic = receiverPicUrl
                )
            } else {
                ChatHomeDirections.actionChatHomeToGroupChatFragment(
                    id, title, senderPicUrl
                )
            }
            findNavController().navigate(action)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDropDownItemClicked(dropDownItem: DropDownItem, recentChat: RecentChat) {
        when (dropDownItem.text) {
            "leave" -> {
                TODO("Fix Leave Not working")
                viewModel.leaveGroup(recentChat.id)
            }
        }
    }
}





