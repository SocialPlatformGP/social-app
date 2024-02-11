package com.gp.auth.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.gp.auth.R
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN: Int = 1
    private lateinit var gso: GoogleSignInOptions
    private lateinit var composeView: ComposeView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return  ComposeView(requireContext()).also {
            composeView =it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            composeView.setContent {
                LoginScreen(
                    viewModel = viewModel,
                    navigateToSignUp = {
                        findNavController().navigate(R.id.action_loginFragment_to_registerationFragment)
                    },
                    navigateToForgotPassword = {
                        findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
                    },
                    onSignInClicked = {
                        onSignInClick()
                    },
                    onSignInWithGoogle = {
                        onSignInWithGoogleClick()
                    },

                )
            }
        createRequest()
    }
    private fun createRequest() {
        // Configure Google Sign In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception=task.exception
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                lifecycleScope.launch {
                    viewModel.authenticateWithGoogle(account).flowWithLifecycle(lifecycle).collect{
                        when(it){
                            is State.SuccessWithData -> {
                                val intent = Intent()
                                intent.setClassName("com.gp.socialapp", "com.gp.socialapp.MainActivity")
                                startActivity(intent)
                                activity?.finish()
                            }

                            else -> {}
                        }

                    }
                }
            }
            catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT)
                    .show()
            }

        }
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
                                activity?.finish()
                            }

                            else -> {}
                        }
                    }
            }

    }







    fun onSignInWithGoogleClick(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

}