package com.gp.chat.presentation.createGroupChat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gp.chat.R
import com.gp.chat.adapter.GroupUserAdapter
import com.gp.chat.databinding.FragmentCreateGroupChatBinding
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.socialapp.utils.State
import com.gp.users.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateGroupChatFragment : Fragment(), OnGroupMembersChangeListener {
    private val viewModel: CreateGroupChatViewModel by viewModels()
    private lateinit var binding: FragmentCreateGroupChatBinding
    private val galleryImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        binding.groupAvatarImageview.setImageURI(it)
        Log.d("zarea3", " in fragment result: $it")
        viewModel.uiState.value = viewModel.uiState.value.copy(avatarURL = it.toString())
        Log.d("zarea3", " in fragment result2: ${viewModel.uiState.value.avatarURL}")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_create_group_chat, container, false
        )
        binding.viewmodel = viewModel
        binding.fragment = this
        binding.onMembersChangeListener = this
        binding.lifecycleOwner = viewLifecycleOwner
        Log.d("EDREES", "onCreateView: ")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("EDREES", "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        val adapter = GroupUserAdapter(requireContext(), this)
        binding.usersRecyclerview.adapter = adapter
        var isUsersLoaded = false
        lifecycleScope.launch {
            viewModel.users.flowWithLifecycle(lifecycle).collect {
                if (it.isNotEmpty()) {
                    adapter.submitList(it)
                    isUsersLoaded = true
                }
            }
        }
        binding.searchTextinput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUsersLoaded && s.toString().isNotBlank()) {
                    adapter.filterList(viewModel.users.value, s.toString())
                } else if (s.toString().isBlank()) {
                    adapter.submitList(viewModel.users.value)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
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
        if (binding.groupNameTextfield.editText!!.text.isBlank()) {
            binding.groupNameTextfield.error = "Group name is required"
            binding.groupNameTextfield.editText!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.groupNameTextfield.error = null
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        } else {
            Log.d("EDREES", "onCreateGroupClick: ")
            lifecycleScope.launch {
                viewModel.createGroup().flowWithLifecycle(lifecycle).collectLatest {
                    when (it) {
                        is State.SuccessWithData -> {

                            val action =
                                CreateGroupChatFragmentDirections.actionCreateGroupChatFragmentToGroupChatFragment(
                                    it.data, viewModel.uiState.value.name, viewModel.uiState.value.avatarURL
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

    override fun onAddGroupMember(user: User) {
        viewModel.addMember(user)
    }

    override fun onRemoveGroupMember(user: User) {
        viewModel.removeMember(user)
    }
}