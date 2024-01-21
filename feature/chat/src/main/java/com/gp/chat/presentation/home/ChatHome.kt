package com.gp.chat.presentation.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.adapter.ChatAdapter
import com.gp.chat.listener.OnItemClickListener
import com.gp.chat.listener.OnRecentChatClicked
import com.gp.chat.model.Message
import com.gp.chat.model.RecentChat
import com.gp.socialapp.database.model.UserEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class ChatHome : Fragment(), OnRecentChatClicked {
    lateinit var recyclerView: RecyclerView
    lateinit var floatingActionButton: FloatingActionButton
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                ChatHomeScreen(
                    viewModel = viewModel,
                    onRecentChatClicked = {recentChat->
                        onClick(recentChat)
                    },
                    onRecentChatLongClicked = {recentChat ->
                        onLongClick(recentChat)
                    },
                    onFabClick = {
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
                    id,
                    title,
                    senderPicUrl
                )
            }
            findNavController().navigate(action)
        }
    }

    override fun onLongClick(recentChat: RecentChat) {
        if (!recentChat.privateChat) {
            TODO("Show Popup menu with leave option")
        }
    }
}





