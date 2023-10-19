package com.gp.posts.presentation.postsSearch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gp.posts.R
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.databinding.FragmentSearchBinding
import com.gp.posts.listeners.VotesClickedListener
import com.gp.socialapp.database.model.PostEntity
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [Search_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class Search_Fragment : Fragment() , VotesClickedListener {
    // TODO: Rename and change types of parameters
    lateinit var  binding: FragmentSearchBinding
    private val viewModel:SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding=FragmentSearchBinding.bind(view)
        val searchAdapter=FeedPostAdapter(this)
        viewModel.queryPosts.observe(viewLifecycleOwner){
            searchAdapter.submitList(it)
        }
        var searchText=binding.tvHeading.text.toString()
        binding.apply {
            binding.rvSearchPosts.apply {
               adapter=searchAdapter
                layoutManager=LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        setHasOptionsMenu(true)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu,menu)
//        val searchItem=menu.findItem(R.id.actionSearch)
//        val searchView=searchItem.actionView as SearchView
//        searchView.onQueryTextChanged {
//            //to update search query
//            viewModel.searchQuery.value=it
//
//
//        }
//    }

    override fun onUpVoteClicked(post: PostEntity) {
        TODO("Not yet implemented")
    }

    override fun onDownVoteClicked(post: PostEntity) {
        TODO("Not yet implemented")
    }

    override fun onPostClicked(post: PostEntity) {
        TODO("Not yet implemented")
    }

}



