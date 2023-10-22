package com.gp.posts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.adapter.SuggestPostAdapter
import com.gp.posts.databinding.FragmentSearchBinding
import com.gp.posts.databinding.FragmentSuggestPostBinding
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.VotesClickedListener
import com.gp.posts.presentation.postsSearch.SearchViewModel
import com.gp.socialapp.database.model.PostEntity
import com.gp.socialapp.database.model.ReplyEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [suggest_post.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class suggest_post : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    lateinit var binding: FragmentSuggestPostBinding
    lateinit var btnSearch: Button
    var searchText :String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggest_post, container, false)
        val view = binding.root

        val btnSearch = view.findViewById<Button>(R.id.btn_search)
        btnSearch.setOnClickListener {
            val action=suggest_postDirections.actionSuggestPostToSearchFragment(searchText)
            Navigation.findNavController(view).navigate(action)

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvSuggestPosts
        val adapter = SuggestPostAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            viewModel.searchResult.flowWithLifecycle(lifecycle).collect {

                binding.rvSuggestPosts.visibility = View.VISIBLE
                adapter.submitList(it)

            }
        }
        val searchView =binding.suggestView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchText=query!!
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText=newText!!

                if(newText.isNullOrEmpty()){
                    binding.rvSuggestPosts.visibility = View.GONE
                    return true
                }
                else{
                    binding.rvSuggestPosts.visibility = View.VISIBLE
                    viewModel.searchPosts(newText)
                    return true
                }}
        })
    }
    }