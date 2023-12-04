package com.gp.chat.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gp.chat.R
import com.gp.chat.adapter.ChatAdapter
import com.gp.chat.listener.OnItemClickListener
import com.gp.chat.listener.OnRecentChatClicked
import com.gp.chat.model.Message
import com.gp.socialapp.database.model.UserEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatHome : Fragment(), OnRecentChatClicked {
    lateinit var recyclerView: RecyclerView
    lateinit var chatAdapter: ChatAdapter
    lateinit var floatingActionButton: FloatingActionButton
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chatListRecyclerView)
        floatingActionButton = view.findViewById(R.id.fabNewChat)
        chatAdapter = ChatAdapter(this)
        recyclerView.adapter = chatAdapter

        lifecycleScope.launch {
            viewModel.recentChats.flowWithLifecycle(lifecycle).collect {
                chatAdapter.submitList(it)
            }
        }

        floatingActionButton.setOnClickListener {
            val action = ChatHomeDirections.actionChatHomeToNewChat()
            findNavController().navigate(action)
        }
    }

    override fun onRecentChatClicked(
        chatId: String,
        receiverName: String,
        senderName: String,
        receiverImage: String,
        isPrivateChat: Boolean,
        senderPicUrl: String,
        receiverPicUrl: String
    ) {


        Log.d(
            "ChatHome",
            "onRecentChatClicked ${chatId + receiverName + senderName + receiverImage + isPrivateChat}"
        )
        val action = if (isPrivateChat) {
            ChatHomeDirections.actionChatHomeToPrivateChatFragment(
                chatId = chatId,
                senderName = senderName,
                receiverName = receiverName,
                senderPic = senderPicUrl,
                receiverPic = receiverPicUrl
            )
        } else {
            ChatHomeDirections.actionChatHomeToGroupChatFragment(chatId)
        }
        findNavController().navigate(action)
    }

    override fun leaveGroup(groupId: String) {
        viewModel.leaveGroup(groupId)
    }


}





