package com.gp.chat.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gp.chat.R
import com.gp.chat.adapter.ChatAdapter
import com.gp.chat.listener.OnItemClickListener
import com.gp.chat.model.Message
import com.gp.socialapp.database.model.UserEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatHome : Fragment(),OnItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var chatAdapter: ChatAdapter
    lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView=view.findViewById(R.id.chatListRecyclerView)
        floatingActionButton=view.findViewById(R.id.fabNewChat)

        var arrayList:ArrayList<Message> = arrayListOf()
        chatAdapter=ChatAdapter(this)
        chatAdapter.setData(arrayList)

        recyclerView.adapter=chatAdapter
        floatingActionButton.setOnClickListener{
            val action =ChatHomeDirections.actionChatHomeToNewChat()
            findNavController().navigate(action)


        }
    }

    override fun onClick(user: UserEntity) {
        TODO("Not yet implemented")
    }


}





