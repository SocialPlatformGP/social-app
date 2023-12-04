package com.gp.chat.presentation.groupchat

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentGroupChatBinding
import com.gp.chat.listener.OnMessageClickListener
import com.gp.chat.listener.MyScrollToBottomObserver
import com.gp.chat.listener.OnFileClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment : Fragment() ,OnMessageClickListener, OnFileClickListener{
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
        val adapter = GroupMessageAdapter("",this,this)
        val manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        binding.recyclerGchat.layoutManager = manager
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(
                binding.recyclerGchat,
                adapter,
                manager
            )
        )
        binding.recyclerGchat.adapter = adapter
        lifecycleScope.launch {
            viewModel.chatMessagesFlow.flowWithLifecycle(lifecycle).collectLatest {
                Log.d("edrees", "before submit")
                adapter.submitList(it)
                Log.d("edrees", "after submit")
                binding.recyclerGchat.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        binding.recyclerGchat.viewTreeObserver.removeOnPreDrawListener(this)
                        binding.recyclerGchat.scrollToPosition(adapter.itemCount - 1)
                        return true
                    }
                })
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
        val editText = EditText(requireContext())
        val dialogBuilder = AlertDialog.Builder(requireContext())
        editText.text.append(body)

        // Set up the dialog properties
        dialogBuilder.setTitle("Edit Message Body")
            .setMessage("Edit your message:")
            .setCancelable(true)
            .setView(editText)
            .setPositiveButton("Save") { dialogInterface: DialogInterface, i: Int ->
                viewModel.updateMessage(messageId,chatId,editText.text.toString())
                Log.d("TAGf", "updateMessage: ${editText.text.toString()}")
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

    override fun onFileClick(fileURL: String, fileType: String, fileNames: String) {
        TODO("Not yet implemented")
    }
}