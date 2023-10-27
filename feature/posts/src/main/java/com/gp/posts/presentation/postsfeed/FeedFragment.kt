package com.gp.posts.presentation.postsfeed

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.R
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.adapter.StateWIthLifeCycle
import com.gp.posts.databinding.FragmentFeedBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() , VotesClickedListener, OnMoreOptionClicked {
    lateinit var  binding:FragmentFeedBinding
    private val viewModel: FeedPostViewModel by viewModels()
    private val currentUser= Firebase.auth.currentUser


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed,container,false)
        binding.stateWithLifecycle= StateWIthLifeCycle(viewModel.uiState, lifecycle = lifecycle)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedAdapter=FeedPostAdapter(this,this, requireContext())
           binding.postsRecyclerView.apply {
                adapter=feedAdapter
               itemAnimator=null
                layoutManager=LinearLayoutManager(requireContext())
           }
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).collect{currentState->
                if(currentState is State.SuccessWithData){
                    Log.d("TAG258", "onViewCreated: ${currentState.data}")
                    feedAdapter.submitList(currentState.data)
                }
            }
        }

        view.findViewById<FloatingActionButton?>(R.id.floatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }
    }

    override fun onPostClicked(post: Post) {
        val action = FeedFragmentDirections.actionFeedFragmentToPostDetialsFragment(post)
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpVoteClicked(post: Post) {
        viewModel.upVote(post)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDownVoteClicked(post: Post) {
        viewModel.downVote(post)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMoreOptionClicked(imageView5: MaterialButton, postitem: Post) {
        var resourceXml=R.menu.extra_option_menu
        if(currentUser?.email!=postitem.authorEmail){
            resourceXml=R.menu.extra_option_menu_not_owner
        }
        val popupMenu = PopupMenu(requireActivity(), imageView5)
        popupMenu.menuInflater.inflate(resourceXml, popupMenu.menu)

        // Set item click listener for the popup menu
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_save -> {
                        // Handle Item 1 click
                        // Add your code here
                        true
                    }
                    R.id.item_delete -> {
                        viewModel.deletePost(postitem)
                        true
                    }
                    R.id.item_report -> {
                        true
                    }
                    R.id.item_edit -> {
                        val action = FeedFragmentDirections.actionFeedFragmentToEditPostFragment(postitem)
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
            // Show the popup menu
            popupMenu.show()
    }

    override fun onMoreOptionClicked(imageView5: MaterialButton, reply: Reply) {
        TODO("Not yet implemented")
    }




}