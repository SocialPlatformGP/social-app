package com.gp.chat.presentation.groupchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentGroupChatBinding
import com.gp.chat.listener.OnMessageClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment : Fragment() ,OnMessageClickListener{
    private val viewModel: GroupChatViewModel by viewModels()
    private lateinit var binding: FragmentGroupChatBinding
    private val args :GroupChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_chat, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GroupMessageAdapter(requireContext(),this)
        binding.recyclerGchat.adapter = adapter
        lifecycleScope.launch {
            viewModel.chatMessagesFlow.flowWithLifecycle(lifecycle).collectLatest {
                Log.d("edrees", "before submit")
                adapter.submitList(it)
                Log.d("edrees", "after submit")
            }
        }
        viewModel.fetchGroupChatMessages(args.groupId)
    }
    fun onSendMessageClick(){
        viewModel.onSendMessage(args.groupId)
    }

    override fun deleteMessage(messageId: String, chatId: String) {
        viewModel.deleteMessage(messageId,chatId)
    }

    override fun updateMessage(messageId: String, chatId: String,body:String) {
        viewModel.updateMessage(messageId,chatId,"")
    }
}