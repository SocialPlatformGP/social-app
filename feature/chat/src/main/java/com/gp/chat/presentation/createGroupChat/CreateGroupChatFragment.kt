package com.gp.chat.presentation.createGroupChat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateGroupChatFragment : Fragment(){
    private val viewModel: CreateGroupChatViewModel by viewModels()
    private lateinit var composeView: ComposeView
    private val galleryImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        Log.d("zarea3", " in fragment result: ${it.toString()}")
        viewModel.updateAvatarURL(it.toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("EDREES", "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme {
                CreateGroupChatScreen(
                    viewModel = viewModel,
                    onChoosePhotoClicked = {
                        onLoadPictureClick()
                    },
                    onCreateGroupClicked = { onCreateGroupClick() })
            }
        }
    }

    fun onLoadPictureClick() {
        val items = arrayOf("Take Photo", "Choose Existing Photo")
        MaterialAlertDialogBuilder(requireContext()).setItems(items) { dialog, which ->
            when (which) {
                0 -> {
                    onTakePhotoSelected()
                }

                1 -> {
                    onChoosePhotoSelected()
                }
            }
        }.show()
    }

    private fun onTakePhotoSelected() {
        TODO("implement camera capturing")
    }

    private fun onChoosePhotoSelected() {
        galleryImageResultLauncher.launch("image/*")
    }

    fun onCreateGroupClick() {
        Log.d("EDREES", "onCreateGroupClick: ${viewModel.selectedUsers.value.map { it.email }}")
        lifecycleScope.launch {
            viewModel.createGroup().flowWithLifecycle(lifecycle).collectLatest {
                when (it) {
                    is State.SuccessWithData -> {

                        val action =
                            CreateGroupChatFragmentDirections.actionCreateGroupChatFragmentToGroupChatFragment(
                                it.data,
                                viewModel.name.value,
                                viewModel.avatarURL.value
                            )
                        findNavController().navigate(action)
                    }

                    is State.Error -> {
                        Log.e("EDREES", "createGroup() failed: ${it.message}")
                    }

                    else -> {}
                }
            }
        }
    }
}