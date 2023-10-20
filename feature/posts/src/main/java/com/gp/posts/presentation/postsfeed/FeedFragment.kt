package com.gp.posts.presentation.postsfeed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gp.posts.R
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.adapter.StateWIthLifeCycle
import com.gp.posts.databinding.FragmentFeedBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() , VotesClickedListener, OnMoreOptionClicked {
    lateinit var  binding:FragmentFeedBinding
    private val viewModel: FeedPostViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed,container,false)
        binding.stateWithLifecycle= StateWIthLifeCycle(viewModel.uiState, lifecycle = lifecycle)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedAdapter=FeedPostAdapter(this,this)
           binding.postsRecyclerView.apply {
                adapter=feedAdapter
               itemAnimator=null
                layoutManager=LinearLayoutManager(requireContext())
           }
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).collect{currentState->
                if(currentState is State.SuccessWithData){
                    feedAdapter.submitList(currentState.data)
                }
            }
        }

        view.findViewById<FloatingActionButton?>(R.id.floatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }
    }

    override fun onPostClicked(post: PostEntity) {
        val action = FeedFragmentDirections.actionFeedFragmentToPostDetialsFragment(post)
        findNavController().navigate(action)
    }

    override fun onUpVoteClicked(post: PostEntity) {
        Log.d("im in FeedFragment", "onUpVoteClicked: ${post.upvotes} ")
        viewModel.upVote(post)
    }

    override fun onDownVoteClicked(post: PostEntity) {
        viewModel.downVote(post)
    }

    override fun onMoreOptionClicked(imageView5: MaterialButton, postitem: PostEntity) {
            val popupMenu = PopupMenu(requireActivity(), imageView5)
            popupMenu.menuInflater.inflate(R.menu.extra_option_menu, popupMenu.menu)

            // Set item click listener for the popup menu
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_1 -> {
                        // Handle Item 1 click
                        // Add your code here
                        true
                    }
                    R.id.menu_item_2 -> {
                        // Handle Item 2 click
                        // Add your code here
                        true
                    }
                    R.id.menu_item_3 -> {
                        viewModel.deletePost(postitem)
                        true
                    }
                    else -> false
                }
            }

            // Show the popup menu
            popupMenu.show()
    }

    override fun onMoreOptionClicked(imageView5: MaterialButton, reply: ReplyEntity) {
        TODO("Not yet implemented")
    }


}