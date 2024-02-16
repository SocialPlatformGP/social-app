package com.gp.posts.presentation.postsfeed

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gp.material.utils.FileManager
import com.gp.posts.R
import com.gp.posts.presentation.feedUiEvents.PostEvent
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

//val taps = arrayOf(
//    "VIP",
//    "All"
//)

@AndroidEntryPoint
class MainFeedFragment : Fragment() {
    private val viewModel: FeedPostViewModel by viewModels()
    lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            AppTheme {
                MainFeedScreen(
                    viewModel = viewModel,
                    postEvent = { action ->
                        when (action) {
                            is PostEvent.OnAddPost -> {
                                findNavController().navigate(R.id.action_mainFeedFragment2_to_createPostFragment)
                            }

                            is PostEvent.OnPostClicked -> {
                                val action = MainFeedFragmentDirections.mainFeedFragment2ToPostDetialsFragment(action.post)
                                findNavController().navigate(action)
                            }
                            is PostEvent.OnPostEdited -> {
                                val action = MainFeedFragmentDirections.mainFeedFragment2ToEditPostFragment(action.post)
                                findNavController().navigate(action)
                            }
                            is PostEvent.OnTagClicked -> {
                                val action = MainFeedFragmentDirections.mainFeedFragment2ToSearchFragment2(
                                    action.tag.label,
                                    true
                                )
                                findNavController().navigate(action)
                            }
                            is PostEvent.OnAudioClicked -> {
                                onFileClicked(action.attachment)
                            }
                            is PostEvent.OnDocumentClicked -> {
                                onFileClicked(action.attachment)
                            }
                            is PostEvent.OnImageClicked -> {
                                onFileClicked(action.attachment)
                            }
                            is PostEvent.OnVideoClicked -> {
                                onFileClicked(action.attachment)
                            }
                            else -> {}

                        }
                    }
                )
            }

        }
    }

    private fun onFileClicked(attachment: PostAttachment) {
        val fileManager = FileManager(requireContext())
        fileManager.downloadFile(
            attachment.url,
            attachment.name,
            MimeType.findByReadableType(attachment.type).value
        )
    }

}