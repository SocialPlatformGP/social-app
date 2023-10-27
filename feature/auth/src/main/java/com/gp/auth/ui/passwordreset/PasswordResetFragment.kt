package com.gp.auth.ui.passwordreset

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gp.auth.R
import com.gp.auth.databinding.FragmentPasswordResetBinding
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordResetFragment : Fragment() {
    private val viewModel: PasswordResetViewModel by viewModels()
    private lateinit var binding: FragmentPasswordResetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_password_reset, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewmodel = viewModel
        return binding.root
    }

    fun onSendResetEmailClick(){
        viewModel.onSendResetEmail()
        lifecycleScope.launch{
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.sentState == new.sentState
            }.collect{
                when(it.sentState){
                    is State.Success -> {
                        Snackbar.make(requireContext(),
                            binding.root,
                            getString(R.string.reset_email_has_been_sent_please_check_your_inbox),
                            Snackbar.LENGTH_SHORT).show()
                        delay(2000)
                        findNavController().navigate(R.id.action_passwordResetFragment_to_loginFragment)
                    }
                    is State.Error -> {
                        Snackbar.make(requireContext(),
                            binding.root,
                            "Reset Failed: ${(it.sentState as State.Error<Nothing>).message}",
                            Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}