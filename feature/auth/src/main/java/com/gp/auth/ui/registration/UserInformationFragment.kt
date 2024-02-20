package com.gp.auth.ui.registration

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.gp.socialapp.theme.AppTheme
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserInformationFragment : Fragment() {
    private val viewModel: UserInformationViewModel by viewModels()
    private lateinit var composeView: ComposeView
    private val args: UserInformationFragmentArgs by navArgs()
    private val PREFS_FILE_NAME = "shit_fix"
    private val KEY_BOOLEAN_VALUE = "isUserComplete"
    private val galleryImageResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            Log.d("seerde", "onActivityResult: $it")
            viewModel.uiState.value = viewModel.uiState.value.copy(pfpLocalURI = it ?: Uri.EMPTY)
        }

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
            AppTheme {
                Surface {
                    UserInfoScreen(
                        viewModel = viewModel,
                        onProfileImageClicked = { onLoadPictureClick() },
                        onContinueClicked = { createAccount() }

                    )
                }
            }

        }
    }

    fun onLoadPictureClick() {
        val items = arrayOf("Take Photo", "Choose Existing Photo")
        MaterialAlertDialogBuilder(requireContext())
            .setItems(items) { dialog, which ->
                when (which) {
                    0 -> {
                        onTakePhotoSelected()
                    }

                    1 -> {
                        onChoosePhotoSelected()
                    }
                }
            }
            .show()
    }

    private fun onTakePhotoSelected() {
        TODO("implement camera capturing")
    }

    private fun onChoosePhotoSelected() {
        galleryImageResultLauncher.launch("image/*")
    }


    private fun createAccount() {
        viewModel.onCompleteAccount(args.userEmail, args.userPassword)
        makeSnackbar("Account created successfully")
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged { old, new ->
                    old.createdState == new.createdState
                }.collect {
                    when (it.createdState) {
                        is State.Success -> {
                            val intent = Intent()
                            intent.setClassName("com.gp.socialapp", "com.gp.socialapp.MainActivity")
                            Log.d("seerde", "how the fuck did we reach here?")
                            saveBooleanToSharedPreferences(true)
                            startActivity(intent)
                            activity?.finish()
                        }

                        is State.Error -> {
                            makeSnackbar(it.createdState.message)
                        }

                        else -> {}
                    }
                }
        }
        Log.d("edrees", "state: ${viewModel.uiState.value}")
    }

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