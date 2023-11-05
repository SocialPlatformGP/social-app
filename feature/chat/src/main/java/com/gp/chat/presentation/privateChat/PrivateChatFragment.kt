package com.gp.chat.presentation.privateChat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.chat.R
import com.gp.chat.adapter.MessageAdapter
import com.gp.chat.databinding.FragmentPrivateChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrivateChatFragment : Fragment() {
    lateinit var binding: FragmentPrivateChatBinding
    private val viewModel: PrivateChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getMessages()
        binding  = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_private_chat,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerMessage
        val adapter = MessageAdapter()
        recyclerView.adapter = adapter
        lifecycleScope.launch {
            viewModel.messages.flowWithLifecycle(lifecycle).collect{
                adapter.submitList(it)
            }
        }
    }
}