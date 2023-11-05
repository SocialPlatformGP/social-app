package com.gp.chat.presentation.newchat

import NewChatViewModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gp.chat.R
import com.gp.chat.adapter.UsersChatAdapter
import com.gp.socialapp.database.model.UserEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewChat : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var usersChatAdapter: UsersChatAdapter
    private val newChatViewModel: NewChatViewModel by viewModels()
    var arrayList:ArrayList<UserEntity> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_chat, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView=view.findViewById(R.id.usersListRecyclerView)
        usersChatAdapter= UsersChatAdapter()
        recyclerView.adapter=usersChatAdapter
        lifecycleScope.launch {
            newChatViewModel.users.flowWithLifecycle(lifecycle).collect{
                for(user in it)
                arrayList.add(user)
            }
        }
        usersChatAdapter.setData(arrayList)

    }


}