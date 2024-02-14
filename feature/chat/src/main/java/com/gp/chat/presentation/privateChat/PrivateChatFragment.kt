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
class PrivateChatFragment : Fragment(), OnFileClickListener{
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
        return ComposeView(requireContext()).apply{
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chatTitle = if (args.senderPic == viewModel.currentUser.photoUrl.toString()) args.receiverName else args.senderName
        val chatImageUrl = if (args.senderPic == viewModel.currentUser.photoUrl.toString()) args.receiverPic else args.senderPic
        composeView.setContent {
            MaterialTheme {
                ChatScreen(
                    viewModel = viewModel,
                    onChatHeaderClicked = { /*TODO("Navigating to profile")*/ },
                    onBackPressed = { findNavController().popBackStack() },
                    onFileClicked = ::onFileClick,
                    onUserClicked = {/*TODO("navigate to profile")*/ },
                    onAttachFileClicked = { openDocument.launch("*/*") },
                    onAttachImageClicked = {
                        openGallery.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    },
                    onOpenCameraClicked = {
                        /*todo*/
                    },
                    chatTitle = chatTitle,
                    chatImageURL = chatImageUrl,
                    dropDownItems = listOf(DropDownItem("Update"), DropDownItem("Delete")),
                )
            }
        }
    }

    override fun onFileClick(fileURL: String, fileType: String, fileNames: String) {
        fileManager = FileManager(requireContext())
        fileManager.downloadFile(fileURL, fileNames, fileType)
    }
}

