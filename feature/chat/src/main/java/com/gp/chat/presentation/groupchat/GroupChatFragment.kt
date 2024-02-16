package com.gp.chat.presentation.groupchat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gp.chat.presentation.home.DropDownItem
import com.gp.chat.presentation.privateChat.ChatScreen
import com.gp.chat.presentation.theme.AppTheme
import com.gp.material.utils.FileManager
import com.gp.material.utils.FileUtils.getFileName
import com.gp.material.utils.FileUtils.getMimeTypeFromUri
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupChatFragment : Fragment(){
    private lateinit var composeView: ComposeView
    private val viewModel: GroupChatViewModel by viewModels()
    private val args: GroupChatFragmentArgs by navArgs()
    private lateinit var fileManager: FileManager

    @RequiresApi(Build.VERSION_CODES.O)
    private val openDocument =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            it?.let {
                it.forEach { uri ->
                    val mimeType = getMimeTypeFromUri(uri, requireContext())
                    val fileName = getFileName(uri, requireContext())
                    Log.d("TAG", "onViewCreated: $mimeType $fileName")
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setData(args.groupId, args.title, args.photoUrl)
        composeView.setContent {
            AppTheme {
                ChatScreen(
                    viewModel = viewModel,
                    chatTitle = args.title,
                    chatImageURL = args.photoUrl,
                    onChatHeaderClicked = { navigateToGroupDetails(viewModel.checkIfAdmin()) },
                    onBackPressed = { findNavController().popBackStack() },
                    onFileClicked = ::onFileClick,
                    onUserClicked = {/*TODO("navigate to user profile")*/ },
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
                    dropDownItems = listOf(DropDownItem("Update"), DropDownItem("Delete")),
                )
            }
        }
    }

    private fun navigateToGroupDetails(isAdmin: Boolean) {
        val action =
            GroupChatFragmentDirections.actionGroupChatFragmentToGroupDetailsFragment(
                args.groupId,
                isAdmin
            )
        findNavController().navigate(action)
    }

    private fun onFileClick(fileURL: String, fileType: String, fileNames: String) {
        Log.d("TAGRT", "onFileClick: $fileURL $fileType $fileNames")

        fileManager = FileManager(requireContext())
        fileManager.downloadFile(fileURL, fileNames, fileType)
        Log.d("TAGRT", "onFileClick: $fileURL $fileType $fileNames")
    }

}