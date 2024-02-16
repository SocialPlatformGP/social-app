package com.gp.chat.presentation.groupdetails.addGroupMembers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gp.chat.presentation.groupdetails.AddMembersScreen
import com.gp.chat.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMembersFragment : Fragment() {

    private val viewModel: AddMembersViewModel by viewModels()
    private val args: AddMembersFragmentArgs by navArgs()
    private lateinit var composeView: ComposeView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }.also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            launch {
                collectUiState()
            }
            launch {
                collectUiStateForCreation()
            }
        }
        composeView.setContent {
            AppTheme {
                AddMembersScreen(viewModel = viewModel, onAddMembersClicked = { onAddMembers() })
            }
        }
    }

    private suspend fun collectUiState() {
        viewModel.uiState.flowWithLifecycle(lifecycle)
            .distinctUntilChanged { old, new -> old.isAllUsersLoaded == new.isAllUsersLoaded }
            .collect { uiState ->
                if (uiState.isAllUsersLoaded) {
                    val users = args.users.toList()
                    Log.d("seerde", "Users from group details: $users")
                    viewModel.submitGroupUsers(users)
                }
            }
    }

    private suspend fun collectUiStateForCreation() {
        viewModel.uiState.distinctUntilChanged { old, new -> old.isCreated == new.isCreated }
            .collectLatest { uiState ->
                if (uiState.isCreated) {
                    Log.d("seerde", "how did we get here?: ${uiState.isCreated}")
                    val action =
                        AddMembersFragmentDirections.actionAddMembersFragmentToGroupDetailsFragment(
                            args.groupID,
                            true
                        )
                    findNavController().navigate(action)
                }
            }
    }

    fun onAddMembers() {
        Log.d("seerde", "OnAdd from fragment: Group id: ${args.groupID}")
        viewModel.onAddMembersClick(args.groupID)
    }
}