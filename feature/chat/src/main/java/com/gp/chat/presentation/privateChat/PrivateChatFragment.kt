package com.gp.chat.presentation.privateChat

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.MimeTypeMap
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.chat.R
import com.gp.chat.adapter.GroupMessageAdapter
import com.gp.chat.databinding.FragmentPrivateChatBinding
import com.gp.chat.listener.MyOpenDocumentContract
import com.gp.chat.listener.MyScrollToBottomObserver
import com.gp.chat.listener.OnFileClickListener
import com.gp.chat.listener.OnMessageClickListener
import com.gp.chat.model.Message
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PrivateChatFragment : Fragment(), OnMessageClickListener, OnFileClickListener {
    lateinit var adapter: GroupMessageAdapter
    lateinit var binding: FragmentPrivateChatBinding

    private val args: PrivateChatFragmentArgs by navArgs()
    private val viewModel: PrivateChatViewModel by viewModels()
    private val openDocument = registerForActivityResult(MyOpenDocumentContract()) {
        it?.let {
            val extension = getFileExtensionFromUri(it)
            val mimeType = getMimeTypeFromExtension(extension!!)
            val fileName = getFileName(it)
            Log.d("TAG", "onViewCreated: $mimeType $fileName")
            viewModel.sendImage(it, mimeType!!, fileName)
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

    @SuppressLint("Range")
    private fun getFileExtensionFromUri(uri: Uri): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        var extension: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                if (displayName != null) {
                    val lastDot = displayName.lastIndexOf('.')
                    if (lastDot >= 0) {
                        extension = displayName.substring(lastDot + 1)
                    }
                }
            }
        }
        return extension
    }

    private fun getMimeTypeFromExtension(fileExtension: String): String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getMimeTypeFromExtension(fileExtension.toLowerCase(Locale("en", "US")))
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
        viewModel.setReceiverEmail(args.receiverEmail)
        viewModel.setChatId(args.chatId)
        viewModel.getMessages()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerMessage
        val manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        recyclerView.layoutManager = manager
        adapter = GroupMessageAdapter(Firebase.auth.currentUser?.displayName!!, this, this)
        adapter.registerAdapterDataObserver(
            MyScrollToBottomObserver(
                recyclerView,
                adapter,
                manager
            )
        )

        binding.addFileButton.setOnClickListener {
            openDocument.launch(arrayOf("*/*"))
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
        //update display name in firebase auth

        Firebase.auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName("Zarea")
                .setPhotoUri("https://i.redd.it/v0caqchbtn741.jpg".toUri())
                .build()
        )

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

    override fun onFileClick(fileURL: String, fileType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileURL.toUri(), "*/*")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }






}

