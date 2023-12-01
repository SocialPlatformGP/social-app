package com.gp.chat.presentation.privateChat

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.navArgs
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentPrivateChatBinding
import com.gp.chat.listener.MyScrollToBottomObserver
import com.gp.chat.listener.OnMessageClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivateChatFragment : Fragment(),OnMessageClickListener {
    lateinit var binding: FragmentPrivateChatBinding
    private val viewModel: PrivateChatViewModel by viewModels()
    var message=""

    private val args : PrivateChatFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setReceiverEmail(args.receiverEmail)
//        viewModel.setSenderEmail(args.senderEmail)

        viewModel.setChatId(args.chatId)
        viewModel.getMessages()
        binding  = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_private_chat,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerMessage
        val manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        recyclerView.layoutManager = manager
        val adapter = GroupMessageAdapter(requireContext(),this)
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(
                recyclerView,
                adapter,
                manager
            )
        )
        recyclerView.adapter = adapter
        lifecycleScope.launch {
            viewModel.messages.flowWithLifecycle(lifecycle).collect{
                adapter.submitList(it)
                recyclerView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                        return true
                    }
                })
            }
        }
    }

    override fun deleteMessage(messageId:String,chatId:String) {
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


}