package com.gp.chat.presentation.groupdetails

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gp.chat.R
import com.gp.chat.adapter.GroupDetailsMemberAdapter
import com.gp.chat.adapter.GroupUserAdapter
import com.gp.chat.databinding.FragmentGroupDetailsBinding
import com.gp.chat.listener.OnGroupMemberClicked
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.users.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupDetailsFragment : Fragment(), OnGroupMemberClicked {
    private val viewModel: GroupDetailsViewModel by viewModels()
    private lateinit var binding: FragmentGroupDetailsBinding
    private val args: GroupDetailsFragmentArgs by navArgs()
    private val galleryImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        binding.groupAvatarImageview.setImageURI(it)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_details, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GroupDetailsMemberAdapter(requireContext(), this)
        lifecycleScope.launch {
            viewModel.users.flowWithLifecycle(lifecycle).collect {
                adapter.submitList(it)
            }
        }
        viewModel.getUsersList(args.groupId)
        binding.groupMembersRecyclerview.adapter = adapter
    }


    private fun onAddGroupMember(user: User) {
        TODO("Not yet implemented")
    }

    private fun onRemoveGroupMember(user: User) {
        viewModel.removeGroupMember(args.groupId, user)
    }
    fun onEditPictureClick() {
        if(args.isAdmin){
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
    }

    private fun onTakePhotoSelected() {
        TODO("implement camera capturing")
    }

    private fun onChoosePhotoSelected() {
        galleryImageResultLauncher.launch("image/*")
    }

    override fun onMemberClicked(user: User) {
        val isAdmin = args.isAdmin
        val items = if(isAdmin) {
            arrayOf("View Profile", "Message", "Remove from Group")
        } else {
            arrayOf("View Profile", "Message")
        }
        MaterialAlertDialogBuilder(requireContext()).setItems(items) { dialog, which ->
            when (items[which]) {
                "View Profile" -> {
                    //TODO("Navigate to profile")
                }

                "Message" -> {
                    //TODO("Navigate to dms")
                }
                "Remove from Group" -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Are you sure you want to remove ${user.firstName + user.lastName}?")
                        .setPositiveButton("Remove") { dialog, which ->
                            onRemoveGroupMember(user)
                        }.setNegativeButton("Cancel"){_, _ ->
                        }.show()
                }
            }
        }.show()
    }
}