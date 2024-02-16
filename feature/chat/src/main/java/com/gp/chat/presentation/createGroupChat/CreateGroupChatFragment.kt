package com.gp.chat.presentation.createGroupChat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
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
import com.gp.chat.presentation.theme.AppTheme
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateGroupChatFragment : Fragment(){
    private val viewModel: CreateGroupChatViewModel by viewModels()
    private lateinit var composeView: ComposeView
    private val galleryImageResultLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia())
    {
        if(it != null){
            viewModel.updateAvatarURL(it.toString())
        }
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
            AppTheme {
                CreateGroupChatScreen(
                    viewModel = viewModel,
                    onChoosePhotoClicked = {
                        galleryImageResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                    })
            }
        }
        lifecycleScope.launch {
            viewModel.isCreated.flowWithLifecycle(lifecycle).collectLatest {
                if(it){
                    val action =
                        CreateGroupChatFragmentDirections.actionCreateGroupChatFragmentToGroupChatFragment(
                            viewModel.groupID.value,
                            viewModel.name.value,
                            viewModel.avatarURL.value
                        )
                    findNavController().navigate(action)
                } else {
                    Log.d("SEERDE", "onViewCreated: group is not created")
                }
            }
        }
    }
}