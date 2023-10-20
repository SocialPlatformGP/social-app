package com.gp.auth.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.gp.auth.databinding.FragmentLoginBinding
import com.gp.auth.util.Validator
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewmodel = viewModel
        return binding.root
    }
    fun onSignInClick(){
        if(validateInput()){
            viewModel.onSignIn()
            lifecycleScope.launch {
                viewModel.loginStateFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .distinctUntilChanged { old, new ->
                        old.isSignedIn == new.isSignedIn
                    }.collect {
                        when(it.isSignedIn){
                            is State.SuccessWithData<FirebaseUser> -> {
                                val intent = Intent()
                                intent.setClassName("com.gp.socialapp", "com.gp.socialapp.MainActivity")
                                startActivity(intent)
                            }
                            is State.Error ->{
                                makeSnackbar(getString(R.string.invalid_login_credentials))
                                binding.passwordTextField.error = " "
                                binding.passwordTextField.editText!!.addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                        binding.emailTextField.error = null
                                        binding.passwordTextField.error = null
                                    }

                                    override fun afterTextChanged(s: Editable?) {}
                                })
                                binding.emailTextField.error = " "
                                binding.emailTextField.editText!!.addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                        binding.emailTextField.error = null
                                        binding.passwordTextField.error = null
                                    }

                                    override fun afterTextChanged(s: Editable?) {}
                                })
                            }
                            else -> {}
                        }
                    }
            }
        }
    }

    private fun validateInput() = validateEmptyFields() && validateEmailField() && validatePasswordField()

    private fun validatePasswordField(): Boolean {
        return if (Validator.PasswordValidator.validateAll(viewModel.loginStateFlow.value.password)) {
            true
        } else {
            binding.passwordTextField.error = getString(R.string.invalid_password)
            binding.passwordTextField.editText!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.passwordTextField.error = null
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            false
        }
    }

    private fun validateEmailField(): Boolean {
        return if (Validator.EmailValidator.validateAll(viewModel.loginStateFlow.value.email)) {
            true
        } else {
            binding.emailTextField.error = getString(R.string.invalid_email)
            binding.emailTextField.editText!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.emailTextField.error = null
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            false
        }
    }

    private fun validateEmptyFields(): Boolean{
        with(viewModel.loginStateFlow.value){
            return if(email.isNullOrBlank()){
                binding.emailTextField.error = "Field is required"
                binding.emailTextField.editText!!.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        binding.emailTextField.error = null
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
                false
            } else if (password.isNullOrBlank()) {
                binding.passwordTextField.error = "Field is required"
                binding.emailTextField.editText!!.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        binding.emailTextField.error = null
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
                false
            } else true
        }
    }

    fun onForgotPasswordClick() = findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
    fun onSignUpClick() = findNavController().navigate(R.id.action_loginFragment_to_registerationFragment)
    private fun makeSnackbar(text: String) = Snackbar.make(requireContext(),binding.root, text, Snackbar.LENGTH_SHORT).show()
}