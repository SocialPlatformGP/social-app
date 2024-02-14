package com.gp.posts.presentation.postDetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostDetailsFragment
    : Fragment(){

    val viewModel: PostDetailsViewModel by viewModels()
    val args: PostDetailsFragmentArgs by navArgs()
    lateinit var composeView: ComposeView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.getRepliesById(args.post.id)
        viewModel.getPost(args.post)
        return ComposeView(requireContext()).also {
            composeView = it
        }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            PostDetailsScreen(viewModel = viewModel)
        }
    }

}