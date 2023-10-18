package com.gp.posts.presentation.createpost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gp.posts.R
import com.gp.posts.databinding.FragmentCreatePostBinding
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: CreatePostViewModel by viewModels()
    lateinit var binding: FragmentCreatePostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_create_post, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).collect {
                when (it.createdState) {
                    is State.Success -> {
                        findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        viewModel.uiState.value.createdState = State.Idle
                        Toast.makeText(
                            requireContext(),
                            "Post Created Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is State.Error -> {
                        Toast.makeText(
                            requireContext(),
                            (it.createdState as State.Error<Nothing>).message, Toast.LENGTH_SHORT
                        ).show()
                        viewModel.uiState.value.createdState = State.Idle

                    }

                    else -> {}

                }
            }
        }
    }

}