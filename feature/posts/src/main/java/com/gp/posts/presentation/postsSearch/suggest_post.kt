package com.gp.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.posts.adapter.SuggestPostAdapter
import com.gp.posts.databinding.FragmentSuggestPostBinding
import com.gp.posts.listeners.OnSuggestedPostClickListener
import com.gp.posts.presentation.postsSearch.SearchViewModel
import com.gp.socialapp.model.Post
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class suggest_post : Fragment(), OnSuggestedPostClickListener {
    private val viewModel: SearchViewModel by viewModels()
    lateinit var binding: FragmentSuggestPostBinding
    var searchText: String = ""


    fun updateSearchQuery(query: String?) {
        searchText = query!!

        if (query.isNullOrEmpty()) {
            binding.rvSuggestPosts.visibility = View.GONE
        } else {
            binding.rvSuggestPosts.visibility = View.VISIBLE
            viewModel.searchPostsByTitle(query)
        }
    }

    fun navigateToFinalResult(query: String?) {
        val action = suggest_postDirections.actionSuggestPostToSearchFragment2(query!!)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_suggest_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvSuggestPosts
        val adapter = SuggestPostAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            viewModel.searchResult.flowWithLifecycle(lifecycle).collect {
                binding.rvSuggestPosts.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        }
    }

    override fun onClick(model: Post) {
        val action = suggest_postDirections.actionSuggestPostToSearchFragment2(model.title, false)
        findNavController().navigate(action)
    }
}