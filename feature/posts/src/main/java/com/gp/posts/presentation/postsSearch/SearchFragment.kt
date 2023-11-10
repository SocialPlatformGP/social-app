package com.gp.posts.presentation.postsSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.posts.R
import com.gp.posts.adapter.SearchResultAdapter
import com.gp.posts.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(){
    private val viewModel: SearchViewModel by viewModels()
    lateinit var binding: FragmentSearchBinding
    private  val args: SearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
    fun backToSuggest(query: String?) {
        findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvSearchPosts
        val adapter = SearchResultAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager= LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            viewModel.searchResult.flowWithLifecycle(lifecycle).collect {
                binding.rvSearchPosts.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        }
        viewModel.searchPosts(args.SearchQuery)
        binding.searchView.text="Search Results for : "+ args.SearchQuery

    }
}