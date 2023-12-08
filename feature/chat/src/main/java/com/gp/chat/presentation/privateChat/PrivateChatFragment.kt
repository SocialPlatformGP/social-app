package com.gp.chat.presentation.privateChat

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentPrivateChatBinding
import com.gp.chat.listener.ImageClickListener
import com.gp.material.utils.MyOpenActionContract
import com.gp.chat.utils.MyScrollToBottomObserver
import com.gp.chat.listener.OnFileClickListener
import com.gp.chat.listener.OnMessageClickListener
import com.gp.material.utils.FileManager
import com.gp.material.utils.FileUtils.getFileName
import com.gp.material.utils.FileUtils.getMimeTypeFromUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PrivateChatFragment : Fragment(), OnMessageClickListener, OnFileClickListener,
    ImageClickListener {
    lateinit var adapter: GroupMessageAdapter
    lateinit var binding: FragmentPrivateChatBinding
    private lateinit var fileManager: FileManager
    private val args: PrivateChatFragmentArgs by navArgs()
    private val viewModel: PrivateChatViewModel by viewModels()
    private val openDocument = registerForActivityResult(MyOpenActionContract()) {
        it?.let {
            it.forEach { uri ->
                val mimeType = getMimeTypeFromUri(uri, requireContext())
                val fileName = getFileName(uri, requireContext())
                Log.d("TAG", "onViewCreated: $mimeType $fileName $uri")
                viewModel.sendFile(uri, mimeType!!, fileName)
            }

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initializeViewModel()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_private_chat, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerMessage
        val manager = LinearLayoutManager(requireContext())

        adapter = GroupMessageAdapter(
            this,
            this,
            this,
            true
        )
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
        //set the title to receiver name
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        if (args.senderName == Firebase.auth.currentUser?.displayName) {
            toolbar.title = args.receiverName

        } else {
            toolbar.title = args.senderName

        }


        lifecycleScope.launch {
            viewModel.messages.flowWithLifecycle(lifecycle).collect {
                adapter.submitList(it)
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
                Log.d("TAGf", "updateMessage: ${editText.text}")
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

    }

    override fun onImageClick(imageUrl: String) {
        val action =
            PrivateChatFragmentDirections.actionPrivateChatFragmentToFullScreenImageDialogFragment(
                imageUrl
            )
        findNavController().navigate(action)
    }

    private fun initializeViewModel() {
        viewModel.setData(
            args.chatId,
            args.senderName,
            args.receiverName,
            args.senderPic,
            args.receiverPic
        )
    }


}

