package com.gp.auth.ui.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.gp.auth.R
import com.gp.auth.databinding.FragmentRegistrationBinding
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.xml.validation.Validator

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()
    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewmodel = viewModel
        return binding.root
    }

    fun onSignUpClick() {
        if (validateInput()) {
            viewModel.onSignUp()
            lifecycleScope.launch {
                viewModel.registrationUiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .distinctUntilChanged { old, new ->
                        old.isSignedUp == new.isSignedUp
                    }.collect {
                        when (it.isSignedUp) {
                            is State.SuccessWithData<FirebaseUser> -> {
                                makeSnackbar("User Created Successfully")
                                val action =
                                    RegistrationFragmentDirections.actionRegisterationFragmentToUserInformationFragment(
                                        viewModel.registrationUiState.value.email,
                                        viewModel.registrationUiState.value.password
                                    )
                                findNavController().navigate(action)
                            }

                            is State.Error -> {
                                makeSnackbar("SignUp Failed: ${(it.isSignedUp as State.Error).message}")
                            }

                            else -> {}
                        }
                    }
            }
        }
    }

    private fun validateInput() = validateEmptyFields() && validateEmailField() && validatePasswordField()

    private fun validatePasswordField(): Boolean {
        return if(com.gp.auth.util.Validator.PasswordValidator.validateAll(viewModel.registrationUiState.value.password)) {
            true
        } else {
            binding.passwordTextField.error = "Invalid Password"
            binding.passwordTextField.editText!!.addTextChangedListener {
                binding.passwordTextField.error = null
            }
            false
        }

    }

    private fun validateEmailField(): Boolean {
        return if(com.gp.auth.util.Validator.EmailValidator.validateAll(viewModel.registrationUiState.value.email)) {
            true
        } else {
            binding.emailTextField.error = "Invalid Email"
            binding.emailTextField.editText!!.addTextChangedListener {
                binding.emailTextField.error = null
            }
            false
        }

    }

    private fun validateEmptyFields(): Boolean {
        with(viewModel.registrationUiState.value) {
            if (email.isNullOrBlank()) {
                binding.emailTextField.error = "Email is required"
                binding.emailTextField.editText!!.addTextChangedListener {
                    binding.emailTextField.error = null
                }
                return false
            }
            else if (password.isNullOrBlank()) {
                binding.passwordTextField.error = "Password is required"
                binding.passwordTextField.editText!!.addTextChangedListener {
                    binding.passwordTextField.error = null
                }
                return false
            }
            else if (rePassword.isNullOrBlank()) {
                binding.confirmPasswordTextField.error = "Re-Password is required"
                binding.confirmPasswordTextField.editText!!.addTextChangedListener {
                    binding.confirmPasswordTextField.error = null
                }
                return false
            }
            else if (password != rePassword) {
                binding.confirmPasswordTextField.error = "Password doesn't match"
                binding.confirmPasswordTextField.editText!!.addTextChangedListener {
                    binding.confirmPasswordTextField.error = null
                }
                return false
            }
            else return true
        }
    }




fun onSignInClick() =
    findNavController().navigate(R.id.action_registerationFragment_to_loginFragment)

private fun makeSnackbar(text: String) =
    Snackbar.make(requireContext(), binding.root, text, Snackbar.LENGTH_SHORT).show()

}