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
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentPrivateChatBinding
import com.gp.chat.listener.ImageClickListener
import com.gp.material.utils.MyOpenActionContract
import com.gp.chat.utils.MyScrollToBottomObserver
import com.gp.chat.listener.OnFileClickListener
import com.gp.chat.listener.OnMessageClickListener
import com.gp.material.utils.FileManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PrivateChatFragment : Fragment(), OnMessageClickListener, OnFileClickListener,
    ImageClickListener {
    lateinit var adapter: GroupMessageAdapter
    lateinit var binding: FragmentPrivateChatBinding
    private lateinit var fileManager: com.gp.material.utils.FileManager
    private val args: PrivateChatFragmentArgs by navArgs()
    private val viewModel: PrivateChatViewModel by viewModels()
    private val openDocument = registerForActivityResult(com.gp.material.utils.MyOpenActionContract()) {
        it?.let {
            it.forEach {uri->
                val mimeType = getMimeTypeFromUri(uri)
                val fileName = getFileName(uri)
                Log.d("TAG", "onViewCreated: $mimeType $fileName")
                viewModel.sendImage(uri, mimeType!!, fileName)
            }

        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var fileName = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                fileName = displayName ?: ""
            }
        }
        return fileName
    }

    private fun getMimeTypeFromUri(uri: Uri): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        var mimeType: String? = null

        // Try to query the ContentResolver to get the MIME type
        mimeType = contentResolver.getType(uri)

        if (mimeType == null) {
            // If ContentResolver couldn't determine the MIME type, try getting it from the file extension
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            if (!fileExtension.isNullOrEmpty()) {
                mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
            }
        }

        return mimeType
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initializeViewModel()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_private_chat,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerMessage
        val manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        recyclerView.layoutManager = manager
        adapter = GroupMessageAdapter(
            this,
            this,
            this
        )
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(
                recyclerView,
                adapter,
                manager
            )
        )
        //set the title to receiver name
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = args.receiverName


        binding.addFileButton.setOnClickListener {
            openDocument.launch("*/*")

        }
        recyclerView.adapter = adapter
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
        fileManager = com.gp.material.utils.FileManager(requireContext())
        fileManager.downloadFile(fileURL, fileNames, fileType)

    }

    override fun onImageClick(imageUrl: String) {
        val action =
            PrivateChatFragmentDirections.actionPrivateChatFragmentToFullScreenImageDialogFragment(
                imageUrl
            )
        findNavController().navigate(action)
    }


}

