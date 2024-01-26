package com.gp.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.gp.posts.listeners.OnSuggestedPostClickListener
import com.gp.posts.presentation.postsSearch.SearchResultsViewModel
import com.gp.posts.presentation.postsSearch.ui.SearchScreen
import com.gp.posts.presentation.postsSearch.ui.suggestItem
import com.gp.posts.presentation.postsSearch.ui.suggestScreen
import com.gp.socialapp.model.Post
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(){
    private lateinit var composeView:ComposeView
    private val viewModel: SearchResultsViewModel by viewModels()


    fun navigateToFinalResult(query: String?) {
        val action = SearchFragmentDirections.actionSuggestPostToSearchFragment2(query!!)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).also {
            composeView=it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
          suggestScreen(viewModel = viewModel, showDetails ={
              onClickStar(it)
          } )

        }
    }

     fun onClickStar(model: String) {
        val action = SearchFragmentDirections.actionSuggestPostToSearchFragment2(model, false)
        findNavController().navigate(action)
    }
}