package com.gp.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.gp.posts.presentation.postsSearch.SearchViewModel
import com.gp.posts.presentation.postsSearch.SuggestScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(){
    private lateinit var composeView:ComposeView
    private val viewModel: SearchViewModel by viewModels()


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
            SuggestScreen(viewModel =viewModel,{onClickStar(it)})
        }
    }

     fun onClickStar(model: String) {
        val action = SearchFragmentDirections.actionSuggestPostToSearchFragment2(model, false)
         Log.d("details", "onClickStar: $model")
        findNavController().navigate(action)
    }
}