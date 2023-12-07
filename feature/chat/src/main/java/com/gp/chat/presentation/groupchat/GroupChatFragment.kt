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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentGroupChatBinding
import com.gp.chat.listener.ImageClickListener
import com.gp.chat.listener.OnMessageClickListener
import com.gp.chat.utils.MyScrollToBottomObserver
import com.gp.chat.listener.OnFileClickListener
import com.gp.material.utils.FileManager
import com.gp.material.utils.FileUtils.getFileName
import com.gp.material.utils.FileUtils.getMimeTypeFromUri
import com.gp.material.utils.MyOpenActionContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment : Fragment(), OnMessageClickListener, OnFileClickListener,
    ImageClickListener {
    private val viewModel: GroupChatViewModel by viewModels()
    private lateinit var binding: FragmentGroupChatBinding
    private val args: GroupChatFragmentArgs by navArgs()
    private lateinit var fileManager: FileManager
    private val openDocument = registerForActivityResult(MyOpenActionContract()) {
        it?.let {
            it.forEach { uri ->
                val mimeType = getMimeTypeFromUri(uri, requireContext())
                val fileName = getFileName(uri, requireContext())
                Log.d("TAG", "onViewCreated: $mimeType $fileName")
                viewModel.sendFile(uri, mimeType!!, fileName)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_chat, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setData(args.groupId,args.title,args.photoUrl)
        val recyclerView = binding.recyclerMessage
        val adapter = GroupMessageAdapter(this, this, this, false)
        val manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true

        recyclerView.layoutManager = manager
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(
                recyclerView,
                adapter,
                manager
            )
        )
        recyclerView.adapter = adapter
        binding.addFileButton.setOnClickListener {
            openDocument.launch("*/*")
        }
        //todo add title to toolbar
        //val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        //toolbar.title = args.groupName

        lifecycleScope.launch {
            viewModel.chatMessagesFlow.flowWithLifecycle(lifecycle).collectLatest {
                Log.d("edrees", "before submit")
                adapter.submitList(it)
                Log.d("edrees", "after submit")
                recyclerView.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                        return true
                    }
                })
            }
        }
    }


    override fun deleteMessage(messageId: String, chatId: String) {
        viewModel.deleteMessage(messageId, chatId)
    }

    override fun updateMessage(messageId: String, chatId: String, body: String) {
        val editText = EditText(requireContext())
        val dialogBuilder = AlertDialog.Builder(requireContext())
        editText.text.append(body)

        // Set up the dialog properties
        dialogBuilder.setTitle("Edit Message Body")
            .setMessage("Edit your message:")
            .setCancelable(true)
            .setView(editText)
            .setPositiveButton("Save") { dialogInterface: DialogInterface, i: Int ->
                viewModel.updateMessage(messageId, chatId, editText.text.toString())
                Log.d("TAGf", "updateMessage: ${editText.text.toString()}")
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

    override fun onFileClick(fileURL: String, fileType: String, fileNames: String) {
        Log.d("TAGRT", "onFileClick: $fileURL $fileType $fileNames")

        fileManager = FileManager(requireContext())
        fileManager.downloadFile(fileURL, fileNames, fileType)
        Log.d("TAGRT", "onFileClick: $fileURL $fileType $fileNames")
    }

    override fun onImageClick(imageUrl: String) {
        val action =
            GroupChatFragmentDirections.actionGroupChatFragmentToFullScreenImageDialogFragment(
                imageUrl
            )
        findNavController().navigate(action)
    }
}