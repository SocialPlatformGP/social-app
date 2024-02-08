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
    //lateinit var binding: FragmentEditPostBinding
    private val args : EditPostFragmentArgs  by navArgs()
    private val viewModel:EditPostViewModel by viewModels()
    lateinit var composeView: ComposeView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).also {
            composeView=it
        }
//        binding=DataBindingUtil.inflate(
//            inflater,
//            R.layout.fragment_edit_post,
//            container,
//            false
//        )
//        binding.viewModel=viewModel
//        binding.lifecycleOwner=this
//        viewModel.post.value=args.post
//        viewModel.uiState.value.title=args.post.title
//        viewModel.uiState.value.body=args.post.body
//        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            EditPostScreen(viewModel = viewModel,args.post)
        }

//        binding.applyButton.setOnClickListener {

//            viewModel.updatePost()
//            findNavController().popBackStack()
//        }
    }



}