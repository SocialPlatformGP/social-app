package com.gp.posts.presentation.createpost

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gp.posts.R
import com.gp.posts.databinding.FragmentCreatePostBinding
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : Fragment() {
    private val viewModel: CreatePostViewModel by viewModels()
    lateinit var binding: FragmentCreatePostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(layoutInflater,R.layout.fragment_create_post,container,false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).collect {uiState ->
                Log.d("TAG", "1- Success triggerd in collector")
                when (uiState.createdState) {
                    is State.Success -> {
                        Log.d("TAG", "2- Success triggerd in collector")
                        findNavController().navigate(R.id.action_createPostFragment_to_feedFragment)
                        Toast.makeText(
                            context,
                            getString(R.string.post_created_successfully),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is State.Error -> {
                        Log.e("TAG", (uiState.createdState as State.Error).message)
                        Toast.makeText(
                            context,
                            (uiState.createdState as State.Error).message,
                            Toast.LENGTH_LONG
                        ).show()
                        TODO("Provide an informative error message to the user")
                    }
                    else -> {
                        Log.d("Edrees", "I am stupid")
                    }
                }
            }
        }
    }
}