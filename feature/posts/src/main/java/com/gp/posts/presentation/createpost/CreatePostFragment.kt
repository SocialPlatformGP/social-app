package com.gp.posts.presentation.createpost

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gp.material.utils.FileUtils.getEnumMimeTypeFromUri
import com.gp.material.utils.FileUtils.getFileName
import com.gp.posts.R
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostFile
import com.gp.socialapp.theme.AppTheme
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private lateinit var composeView: ComposeView
    private val viewModel: CreatePostViewModel by viewModels()
    private val args: CreatePostFragmentArgs by navArgs()
    private val openFileResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents())
        { list ->
            list.forEach { uri ->
                if (viewModel.uiState.value.files.any { it.uri == uri }) {
                    makeSnackbar("This File is already added!", Snackbar.LENGTH_LONG)
                } else {
                    viewModel.addFile(
                        PostFile(
                            uri = uri,
                            name = getFileName(uri, requireContext()),
                            type = getEnumMimeTypeFromUri(uri, requireContext())
                        )
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            composeView = this
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            AppTheme {
                Surface {
                    CreatePostScreen(
                        viewModel = viewModel,
                        navigateBack = { viewModel.onCancel() },
                        onAddFileClick = { onOpenFileClick() },
                        onAddImageClick = { onOpenImageClick() },
                        onAddVideoClick = { onOpenVideoClick() },
                        onPreviewFile = { onFilePreviewClicked(it) },
                    )
                }
            }

        }
        viewModel.setType(args.type)
        addCancelPressedCollector()
        addCreatedStateCollector()
    }

    private fun addCreatedStateCollector() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.createdState == new.createdState
            }.collect { uiState ->
                when (uiState.createdState) {
                    is State.Success -> {
                        findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        makeSnackbar(
                            getString(R.string.post_created_successfully),
                            Snackbar.LENGTH_LONG
                        )
                    }

                    else -> {}
                }
            }
        }
    }


    private fun addCancelPressedCollector() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.cancelPressed == new.cancelPressed
            }.collect { uiState ->
                if (uiState.cancelPressed) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Discard post draft?")
                        .setPositiveButton("Discard") { dialog, which ->
                            findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        }.setNegativeButton("Cancel") { _, _ ->
                        }.show()
                    viewModel.resetCancelPressed()
                }
            }
        }
    }


    fun onOpenImageClick() {
        openFileResultLauncher.launch(MimeType.IMAGE.value)
    }

    fun onOpenVideoClick() {
        openFileResultLauncher.launch(MimeType.VIDEO.value)
    }

    fun onOpenFileClick() {
        openFileResultLauncher.launch(MimeType.ALL_FILES.value)
    }

    private fun makeSnackbar(message: String, duration: Int) {
        Snackbar.make(
            requireContext(),
            composeView.rootView,
            message,
            duration
        ).show()
    }

    fun onFilePreviewClicked(file: PostFile) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(file.uri, file.type.value)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            requireContext().startActivity(intent)
        } else {
            makeSnackbar("No app available to open this file format!", Snackbar.LENGTH_SHORT)
        }
    }





}