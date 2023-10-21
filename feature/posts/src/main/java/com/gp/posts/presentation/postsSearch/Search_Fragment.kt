package com.gp.posts.presentation.postsSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.gp.posts.R
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.databinding.FragmentSearchBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Search_Fragment : Fragment(), VotesClickedListener, OnMoreOptionClicked {
    private val viewModel: SearchViewModel by viewModels()
    lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvSearchPosts
        val adapter = FeedPostAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            viewModel.searchResult.flowWithLifecycle(lifecycle).collect {

                binding.rvSearchPosts.visibility = View.VISIBLE
                adapter.submitList(it)

            }
        }
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    binding.rvSearchPosts.visibility = View.GONE
                    return true
                } else {
                    binding.rvSearchPosts.visibility = View.VISIBLE
                    viewModel.searchPosts(newText)
                    return true
                }
            }
        })
    }

    override fun onUpVoteClicked(post: PostEntity) {
    }

    override fun onDownVoteClicked(post: PostEntity) {
    }

    override fun onPostClicked(post: PostEntity) {
    }

    override fun onMoreOptionClicked(imageView5: MaterialButton, postitem: PostEntity) {
    }

    override fun onMoreOptionClicked(imageView5: MaterialButton, reply: ReplyEntity) {
    }

}



