package com.gp.material.presentation

import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gp.material.R
import com.gp.material.listener.ClickOnFileClicKListener
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MaterialFragment : Fragment(){
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
            MaterialTheme {
                MaterialScreen(
                    isAdmin = true,
                    onOpenFile = { item -> openFile(item) },
                    onFolderClicked = { path -> viewModel.openFolder(path) },
                    folderDropDownItems = listOf("Delete"),
                    fileDropDownItem = listOf("Download", "Share", "Details", "Delete"),
                    onDropDownItemClicked = {dropDownItem, item ->
                        if (item.fileType == FileType.FOLDER) {
                            if(dropDownItem == "Delete"){
                                viewModel.deleteFolder(item.path)
                            }
                        } else {
                            when(dropDownItem){
                                "Delete" -> { viewModel.deleteFile(item.path)}
                                "Download" -> {downloadFile(item)}
                                "Share" -> {shareLink(item)}
                                "Details" -> {showDetailsDialog(item)}
                            }
                        }
                    },
                    onBackPressed = {
                        if (viewModel.goBack()) {
                            viewModel.fetchDataFromFirebaseStorage()
                        } else {
                            findNavController().navigateUp()
                        }
                    },
                    viewModel = viewModel,
                    onNewFileClicked = {actionUpload.launch("*/*")}
                )
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
    private fun showDetailsDialog(item: MaterialItem) {
        val context = requireContext()
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Details")

        val message = """
        Path: ${item.path}
        FileType: ${item.fileType}
        Name: ${item.name}
        Created By: ${item.createdBy}
        File URL: ${item.fileUrl}
        Creation Time: ${item.creationTime}
        Size: ${item.size}
    """.trimIndent()

        alertDialogBuilder.setMessage(message)

        alertDialogBuilder.setPositiveButton("Open Link") { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl))
            context.startActivity(intent)
        }

        alertDialogBuilder.setNegativeButton("Copy Link") { _, _ ->
            copyToClipboard("Link", item.fileUrl)
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
        }

        alertDialogBuilder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun copyToClipboard(label: String, text: String) {
        val context = requireContext()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText(label, text)
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


