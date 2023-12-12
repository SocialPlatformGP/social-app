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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = Firebase.auth.currentUser?.displayName
        recyclerView = view.findViewById(R.id.chatListRecyclerView)
        floatingActionButton = view.findViewById(R.id.fabNewChat)
        chatAdapter = ChatAdapter(this)
        recyclerView.adapter = chatAdapter

        lifecycleScope.launch {
            val  formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH)

            viewModel.recentChats.flowWithLifecycle(lifecycle).collect { it ->
                chatAdapter.submitList(it.map {
                    it.copy(
                        timestamp = DateTimeFormatter
                            .ofPattern("hh:mm", Locale.ENGLISH)
                            .format(ZonedDateTime.parse(it.timestamp,formatter))
                    )
                })
            }


        }
        floatingActionButton.setOnClickListener {
            val action = ChatHomeDirections.actionChatHomeToNewChat()
            findNavController().navigate(action)
        }

    }

    override fun onRecentChatClicked(
        recentChat: RecentChat
    ) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun leaveGroup(groupId: String) {
        viewModel.leaveGroup(groupId)
    }


}





