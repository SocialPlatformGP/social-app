package com.gp.chat.presentation.newchat

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.adapter.UsersChatAdapter
import com.gp.chat.listener.OnItemClickListener
import com.gp.chat.util.RemoveSpecialChar
import com.gp.chat.util.RemoveSpecialChar.removeSpecialCharacters
import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewChat : Fragment(), OnItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var usersChatAdapter: UsersChatAdapter
    private val newChatViewModel: NewChatViewModel by viewModels()
    var arrayList: ArrayList<UserEntity> = arrayListOf()
    private val senderEmail = removeSpecialCharacters(Firebase.auth.currentUser?.email!!)

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

        recyclerView = view.findViewById(R.id.usersListRecyclerView)
        usersChatAdapter = UsersChatAdapter(this)
        recyclerView.adapter = usersChatAdapter
        lifecycleScope.launch {
            newChatViewModel.users.flowWithLifecycle(lifecycle).collect {
                usersChatAdapter.submitList(it)
            }
        }


    }


    override fun onClick(userEmail: String) {
        newChatViewModel.createChat(userEmail)
        lifecycleScope.launch {
            newChatViewModel.createNewChatState.flowWithLifecycle(lifecycle).collect {
                when (it) {
                    is State.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }

                    is State.SuccessWithData -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        if (it.data != "-1") {
                            val action = NewChatDirections.actionNewChatToPrivateChatFragment(
                                chatId = it.data,
                                receiverEmail = removeSpecialCharacters(userEmail),
                                senderEmail = senderEmail
                            )
                            findNavController().navigate(action)

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
