package com.gp.auth.ui.passwordreset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.gp.auth.R
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordResetFragment : Fragment() {
    private val viewModel: PasswordResetViewModel by viewModels()
    private lateinit var composeView: ComposeView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            ForgetPasswordScreen(
                viewModel = viewModel,
                onSendResetEmail = { onSendResetEmailClick() }
            )
        }
    }

    fun onSendResetEmailClick() {
        viewModel.onSendResetEmail()
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).distinctUntilChanged { old, new ->
                old.sentState == new.sentState
            }.collect {
                when (it.sentState) {
                    is State.Success -> {
                        Snackbar.make(
                            requireContext(),
                           composeView.rootView,
                            getString(R.string.reset_email_has_been_sent_please_check_your_inbox),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        delay(2000)
                        findNavController().navigate(R.id.action_passwordResetFragment_to_loginFragment)
                    }

                    is State.Error -> {
                        Snackbar.make(
                            requireContext(),
                            composeView.rootView,
                            "Reset Failed: ${(it.sentState as State.Error<Nothing>).message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }
}