package com.gp.chat.presentation.createGroupChat

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gp.chat.R
import com.gp.chat.databinding.FragmentCreateGroupChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGroupChatFragment : Fragment() {
    private val viewModel: CreateGroupChatViewModel by viewModels()
    private lateinit var binding: FragmentCreateGroupChatBinding
    private val galleryImageResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            binding.groupAvatarImageview.setImageURI(it)
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_group_chat, container, false)
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
    fun onCreateGroupClick(){
        TODO()
    }
}