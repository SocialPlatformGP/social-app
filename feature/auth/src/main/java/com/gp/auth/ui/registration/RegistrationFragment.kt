package com.gp.auth.ui.registration

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.gp.auth.R
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var composeView: ComposeView

    private val PREFS_FILE_NAME = "shit_fix"
    private val KEY_BOOLEAN_VALUE = "isUserComplete"

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
            RegistrationScreen(
                viewModel = viewModel,
                onNavigateToLoginScreen = {
                    onSignInClick()
                },
                onCreateAccount = {
                    onSignUpClick()
                }
            )
        }
    }

    fun onSignUpClick() {
        viewModel.onSignUp()
        lifecycleScope.launch {
            viewModel.registrationUiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged { old, new ->
                    old.isSignedUp == new.isSignedUp
                }.collect {
                    when (it.isSignedUp) {
                        is State.SuccessWithData<FirebaseUser> -> {
                            makeSnackbar("User Created Successfully")
                            saveBooleanToSharedPreferences(false)
                            navigateToHomeScreen()
                        }

                        is State.Error -> {
                            makeSnackbar("SignUp Failed: ${(it.isSignedUp as State.Error).message}")
                        }

                        else -> {}
                    }
                }
        }

    }
    private fun navigateToHomeScreen() {
        findNavController().navigate(
            RegistrationFragmentDirections.actionRegisterationFragmentToUserInformationFragment(
                viewModel.registrationUiState.value.email,
                viewModel.registrationUiState.value.password
            )
        )
    }

    private fun onSignInClick() =
        findNavController().navigate(R.id.action_registerationFragment_to_loginFragment)

    private fun makeSnackbar(text: String) =
        Snackbar.make(requireContext(), composeView.rootView, text, Snackbar.LENGTH_SHORT).show()

    private fun saveBooleanToSharedPreferences(value: Boolean) {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(KEY_BOOLEAN_VALUE, value)
        editor.apply()
    }

}