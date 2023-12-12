package com.gp.chat.presentation.groupdetails.addGroupMembers

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.gp.chat.R
import com.gp.chat.adapter.GroupUserAdapter
import com.gp.chat.databinding.DialogAddMembersBinding
import com.gp.chat.listener.OnGroupMembersChangeListener
import com.gp.users.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMembersDialogFragment : DialogFragment(), OnGroupMembersChangeListener {

    private val viewModel: AddMembersDialogViewModel by viewModels()
    private lateinit var binding: DialogAddMembersBinding
    private lateinit var adapter: GroupUserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddMembersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.fragment = this
        binding.onMembersChangeListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = GroupUserAdapter(requireContext(), this)
        binding.usersRecyclerview.adapter = adapter
        lifecycleScope.launch {
            launch{
                collectUiState()
            }
            launch{
                collectUsers()
            }
            launch{
                collectUiStateForCreation()
            }
        }
    }
    private suspend fun collectUiState() {
        viewModel.uiState.flowWithLifecycle(lifecycle)
            .distinctUntilChanged { old, new -> old.isAllUsersLoaded == new.isAllUsersLoaded }
            .collect { uiState ->
                if (uiState.isAllUsersLoaded) {
                    val users = requireArguments().getParcelableArrayList<User>("users")!!.toList()
                    Log.d("seerde", "Users from group details: $users")
                    viewModel.submitGroupUsers(users)
                }
            }
    }

    private suspend fun collectUsers() {
        viewModel.users.flowWithLifecycle(lifecycle)
            .collect { users ->
                Log.d("seerde", "Users set to adapter: $users")
                adapter.submitList(users)
            }
    }

    private suspend fun collectUiStateForCreation() {
        viewModel.uiState.distinctUntilChanged { old, new -> old.isCreated == new.isCreated }
            .collectLatest { uiState ->
                if (uiState.isCreated) {
                    Log.d("seerde", "how did we get here?: ${uiState.isCreated}")
                    dismiss()
                }
            }
    }
    override fun onAddGroupMember(user: User) {
        viewModel.addMember(user)
    }

    override fun onRemoveGroupMember(user: User) {
        viewModel.removeMember(user)
    }
    fun onAddMembers(){
        Log.d("seerde", "OnAdd from fragment: Group id: ${requireArguments().getString("group_id", "")}")
        viewModel.onAddMembersClick(requireArguments().getString("group_id", ""))
    }
    fun onCancelClicked(){
        dismiss()
    }
}