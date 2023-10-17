package com.gp.auth.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.gp.auth.R
import com.gp.auth.databinding.FragmentLoginBinding
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
                        makeSnackbar("Login Failed: ${(it.isSignedIn as State.Error).message}")
                    }
                    else -> {}
                }
            }
        }
    }
    fun onForgotPasswordClick() = findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
    fun onSignUpClick() = findNavController().navigate(R.id.action_loginFragment_to_registerationFragment)
    private fun makeSnackbar(text: String) = Snackbar.make(requireContext(),binding.root, text, Snackbar.LENGTH_SHORT).show()
}