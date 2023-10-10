package com.gp.posts.presentation.postsfeed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.gp.posts.R
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.databinding.FragmentFeedBinding
import com.gp.posts.listeners.PostOnClickListener
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() , VotesClickedListener {
    lateinit var  binding:FragmentFeedBinding
    private val viewModel: FeedPostViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed,container,false)
        binding.lifecycleOwner=this
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedAdapter=FeedPostAdapter(this)
           binding.postsRecyclerView.apply {
                adapter=feedAdapter
               itemAnimator=null
                layoutManager=LinearLayoutManager(requireContext())
           }
        lifecycleScope.launch {
            viewModel.posts.flowWithLifecycle(lifecycle).collect {
                feedAdapter.submitList(it)
            }
        }

        view.findViewById<FloatingActionButton?>(R.id.floatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }
    }

    override fun onPostClicked(post: PostEntity) {
        Snackbar.make(binding.root,"TODO : Navigate to PostDetailsFragment",Snackbar.LENGTH_SHORT).show()
    }

    override fun onUpVoteClicked(post: PostEntity) {
        viewModel.upVote(post)
    }

    override fun onDownVoteClicked(post: PostEntity) {
        viewModel.downVote(post)
    }


}