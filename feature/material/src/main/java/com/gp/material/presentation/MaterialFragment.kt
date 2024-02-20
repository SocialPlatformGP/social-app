package com.gp.material.presentation

import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gp.chat.presentation.theme.AppTheme
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MaterialFragment : Fragment() {
    private lateinit var composeView: ComposeView
    private val viewModel: MaterialViewModel by viewModels()
    private val actionUpload = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            viewModel.uploadFile(it, requireContext())
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            AppTheme {
                Surface {
                    MaterialScreen(
                        isAdmin = true,
                        onOpenFile = { item -> openFile(item) },
                        onFolderClicked = { path ->
                            viewModel.openFolder(path) },
                        onBackPressed = {
                            if (viewModel.goBack()) {
                                viewModel.fetchDataFromFirebaseStorage()
                            } else {
                                findNavController().navigateUp()
                            }
                        },
                        onDownloadFile = ::downloadFile,
                        onShareLink = ::shareLink,
                        viewModel = viewModel,
                        onNewFileClicked = { actionUpload.launch("*/*") }
                    )
                }

            }
        }
        lifecycleScope.launch {
            viewModel.actionState.flowWithLifecycle(lifecycle).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        Snackbar.make(composeView, it.data, Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is State.Error -> {
                        Snackbar.make(composeView, it.message, Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun openFile(item: MaterialItem) {
        val openFileIntent = Intent(Intent.ACTION_VIEW)
        openFileIntent.setDataAndType(Uri.parse(item.fileUrl), item.fileType.getMimeType())
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            requireContext().startActivity(openFileIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app found to open the file", Toast.LENGTH_SHORT).show()
        }
    }


    private fun FileType.getMimeType(): String {
        return when (this) {
            FileType.IMAGE -> "image/*"
            FileType.VIDEO -> "video/*"
            FileType.PDF -> "application/pdf"
            FileType.AUDIO -> "audio/*"
            else -> "*/*"
        }
    }


    private fun downloadFile(item: MaterialItem) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
        requireContext().startActivity(intent)
    }
    private fun String.copyToClipboard(text: String) {
        val context = requireContext()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText(this, text)
        clipboard.setPrimaryClip(clip)
    }


    private fun shareLink(item: MaterialItem) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, item.fileUrl)
            type = "*/*"
        }
        requireContext().startActivity(intent)
    }
}


