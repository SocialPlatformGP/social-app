package com.gp.posts.presentation.createpost

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gp.posts.R
import com.gp.posts.databinding.FragmentCreatePostBinding
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
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
        addCreatedStateCollector()
        addCancelPressedCollector()
        //TODO("implement visiblity option dropdown menu")
    }
    private fun addCreatedStateCollector(){
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
                    is State.Error -> {
                        makeSnackbar(
                            (uiState.createdState as State.Error).message,
                            Snackbar.LENGTH_LONG
                        )
                        //TODO("Provide an informative error message to the user")
                    }
                    else ->{}
                }
            }
        }
    }
    private fun addCancelPressedCollector(){
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.cancelPressed == new.cancelPressed
            }.collect { uiState ->
                if (uiState.cancelPressed) {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Discard post draft?")
                        .setPositiveButton("Discard") { dialog, which ->
                            findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        }.setNegativeButton("Cancel"){_, _ ->
                        }.show()
                    viewModel.resetCancelPressed()
                }
            }
        }
    }

    private fun makeSnackbar(message: String, duration: Int) {
        Snackbar.make(
            requireContext(),
            binding.root,
            message,
            duration
        ).show()
    }
}