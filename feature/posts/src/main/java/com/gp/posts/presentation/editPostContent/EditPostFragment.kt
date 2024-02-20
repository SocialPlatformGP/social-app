package com.gp.posts.presentation.editPostContent

import EditPostScreen
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.gp.material.utils.FileUtils.getEnumMimeTypeFromUri
import com.gp.material.utils.FileUtils.getFileName
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : Fragment() {
    //lateinit var binding: FragmentEditPostBinding
    private val args: EditPostFragmentArgs by navArgs()
    private val viewModel: EditPostViewModel by viewModels()
    lateinit var composeView: ComposeView

    private val openFileResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents())
        { list ->
            list.forEach { uri ->
                if (viewModel.post.value.attachments.any { it.url.toUri() == uri }) {
                    makeSnackbar("This File is already added!", Snackbar.LENGTH_LONG)
                } else {
                    viewModel.addFile(
                        listOf(
                        PostAttachment(
                            url = uri.toString(),
                            name = getFileName(uri, requireContext()),
                            type = getEnumMimeTypeFromUri(uri, requireContext()).readableType,
                        ))
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    fun back() {
        findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            viewModel.updateBody(args.post.body)
            viewModel.updateTitle(args.post.title)
            viewModel.setPost(args.post)
            AppTheme {
                Surface {
                    EditPostScreen(
                        viewModel = viewModel,
                        back = { back() },
                        onPreviewFile = {  onFilePreviewClicked(it) },
                        onAddImageClick = { onOpenImageClick() },
                        onAddVideoClick = { onOpenVideoClick() },
                        onAddFileClick = { onOpenFileClick() },
                    )
                }
            }

        }

    }

    fun onFilePreviewClicked(file: PostAttachment) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(file.url.toUri(), file.type)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            requireContext().startActivity(intent)
        } else {
            makeSnackbar("No app available to open this file format!", Snackbar.LENGTH_SHORT)
        }
    }

    private fun makeSnackbar(message: String, duration: Int) {
        Snackbar.make(
            requireContext(),
            composeView.rootView,
            message,
            duration
        ).show()
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
}