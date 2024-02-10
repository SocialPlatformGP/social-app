package com.gp.posts.presentation.editPostContent

import EditPostScreen
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gp.posts.R
import com.gp.posts.databinding.FragmentEditPostBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPostFragment : Fragment() {
    private val args : EditPostFragmentArgs  by navArgs()
    lateinit var composeView: ComposeView
    private val viewModel:EditPostViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.uiState.value = EditPostUIState(
            title = args.post.title,
            body = args.post.body
        )
        viewModel.post.value = args.post

        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            EditPostScreen(
                viewModel =viewModel,
                onNavigateBack = {
                    findNavController().popBackStack()
                }
            )
        }
    }
}