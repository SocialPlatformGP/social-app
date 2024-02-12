package com.gp.chat.presentation.privateChat

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gp.chat.listener.ImageClickListener
import com.gp.chat.listener.OnFileClickListener
import com.gp.chat.listener.OnMessageClickListener
import com.gp.chat.presentation.home.DropDownItem
import com.gp.material.utils.FileManager
import com.gp.material.utils.FileUtils.getFileName
import com.gp.material.utils.FileUtils.getMimeTypeFromUri
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivateChatFragment : Fragment(), OnMessageClickListener, OnFileClickListener,
    ImageClickListener {
    private lateinit var composeView: ComposeView
    private lateinit var fileManager: FileManager
    private val args: PrivateChatFragmentArgs by navArgs()
    private val viewModel: PrivateChatViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    private val openDocument =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            it?.let {
                it.forEach { uri ->
                    val mimeType = getMimeTypeFromUri(uri, requireContext())
                    val fileName = getFileName(uri, requireContext())
                    Log.d("TAG", "onViewCreated: $mimeType $fileName $uri")
                    viewModel.sendFile(uri, mimeType!!, fileName)
                }

            }
        }
    @RequiresApi(Build.VERSION_CODES.O)
    private val openGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia())
    {
        it?.let { uri ->
            val mimeType = getMimeTypeFromUri(uri, requireContext())
            val fileName = getFileName(uri, requireContext())
            Log.d("TAG", "onViewCreated: $mimeType $fileName $uri")
            viewModel.sendFile(uri, mimeType!!, fileName)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeViewModel() {
        viewModel.setData(
            args.chatId,
            args.senderName,
            args.receiverName,
            args.senderPic,
            args.receiverPic
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initializeViewModel()
        Log.d("SEERDE", "onCreateView")
        return ComposeView(requireContext()).apply{
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("seerde", "1- ${args.chatId} 2- ${args.receiverName} 3- ${args.senderName} 4- ${args.receiverPic} 5- ${args.senderPic}")
        val chatTitle = if (args.senderPic == viewModel.currentUser.photoUrl.toString()) args.receiverName else args.senderName
        val chatImageUrl = if (args.senderPic == viewModel.currentUser.photoUrl.toString()) args.receiverPic else args.senderPic
        Log.d("SEERDE", "onViewCreated: $chatTitle----$chatImageUrl")
        composeView.setContent {
            MaterialTheme {
                ChatScreen(
                    viewModel = viewModel,
                    isPrivateChat = true,
                    onChatHeaderClicked = { /*TODO("Navigating to profile")*/ },
                    onBackPressed = { findNavController().popBackStack() },
                    onFileClicked =  ::onFileClick,
                    onImageClicked = ::onImageClick,
                    onUserClicked = {/*TODO("navigate to profile")*/},
                    onAttachFileClicked = { openDocument.launch("*/*") },
                    onAttachImageClicked = { openGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
                    onOpenCameraClicked = {
                        val action =
                        PrivateChatFragmentDirections.actionPrivateChatFragmentToCameraPreviewFragment(
                            chatId = args.chatId,
                            senderName = args.senderName,
                            receiverName = args.receiverName,
                            senderPic = args.senderPic,
                            receiverPic = args.receiverPic,
                            isPrivateChat = true
                        )
                        findNavController().navigate(action) },
                    chatTitle = chatTitle,
                    chatImageURL = chatImageUrl,
                    dropDownItems = listOf(DropDownItem("Update"), DropDownItem("Delete")),
                )
            }
        }
        Log.d("SEERDE", "onViewCreated: 2")
    }

    override fun onStart() {
        super.onStart()
        Log.d("SEERDE", "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d("SEERDE", "onResume: ")
    }

    private fun onDropDownItemClicked(dropDownItem: DropDownItem, messageId: String, messageBody: String) {
        val chatId = args.chatId
        when (dropDownItem.text) {
            "Delete" -> {
                deleteMessage(messageId, chatId)
            }
            "Update" -> {
                updateMessage(messageId, chatId, messageBody)
            }
        }
    }

    override fun deleteMessage(messageId: String, chatId: String) {
        viewModel.deleteMessage(messageId)
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
                viewModel.updateMessage(messageId, editText.text.toString())
                Log.d("TAGf", "updateMessage: ${editText.text}")
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }


    override fun onFileClick(fileURL: String, fileType: String, fileNames: String) {
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
}

