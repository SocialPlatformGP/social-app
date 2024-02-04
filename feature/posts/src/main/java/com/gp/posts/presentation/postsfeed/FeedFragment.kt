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
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.material.utils.FileManager
import com.gp.posts.R
import com.gp.posts.adapter.FeedPostAdapter
import com.gp.posts.adapter.StateWIthLifeCycle
import com.gp.posts.databinding.BottomSheetFeedOptionsBinding
import com.gp.posts.databinding.FragmentFeedBinding
import com.gp.posts.listeners.OnFeedOptionsClicked
import com.gp.posts.listeners.OnFileClickedListener
import com.gp.posts.listeners.OnMoreOptionClicked
import com.gp.posts.listeners.OnTagClicked
import com.gp.posts.listeners.VotesClickedListenerPost
import com.gp.posts.presentation.postsSearch.ui.suggestScreen
import com.gp.socialapp.database.model.MimeType
import com.gp.socialapp.database.model.PostAttachment
import com.gp.socialapp.model.Post
import com.gp.socialapp.model.Reply
import com.gp.socialapp.model.Tag
import com.gp.socialapp.utils.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment()
   // , VotesClickedListenerPost, OnMoreOptionClicked, OnFeedOptionsClicked , OnTagClicked, OnFileClickedListener
{
//    lateinit var  binding:FragmentFeedBinding
//    lateinit var  feedAdapter: FeedPostAdapter
  private val viewModel: FeedPostViewModel by viewModels()
//    private val currentUser = Firebase.auth.currentUser
    lateinit var composeView: ComposeView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
//
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
//        binding.stateWithLifecycle = StateWIthLifeCycle(viewModel.uiState, lifecycle = lifecycle)
//        binding.onFeedOptionsClicked = this
//        return binding.root

        return ComposeView(requireContext()).also {
            composeView=it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            FeedScreen(viewModel = viewModel)
            }

        }}
//
//        feedAdapter = FeedPostAdapter(this, this, this, this, requireContext())
//        binding.postsRecyclerView.apply {
//            adapter = feedAdapter
//            itemAnimator = null
//            layoutManager = LinearLayoutManager(requireContext())
//        }
//        lifecycleScope.launch {
//            viewModel.uiState.flowWithLifecycle(lifecycle).collect { currentState ->
//                when (currentState) {
//                    is State.SuccessWithData -> {
//                        Log.d(
//                            "seerde",
//                            "onViewCreated: ${currentState.data.map { "${it.title} : ${it.votes}" }}"
//                        )
//                        val vipData = currentState.data.filter { it.type != "vip" }
//                        feedAdapter.submitList(vipData)
//                    }
//
//                    is State.Loading -> {
//                        Log.d("seerde", "onViewCreated: Loading")
//                        feedAdapter.submitList(emptyList())
//                    }
//
//                    else -> Log.d("seerde", "xd?")
//                }
//            }
//
//
//        }
//        binding.floatingActionButton.setOnClickListener {
//            Log.d("TAG258", "onViewCreated:all  ")

//            val action =
//                MainFeedFragmentDirections.actionMainFeedFragment2ToCreatePostFragment("all")
//            findNavController().navigate(action)
//        }
//    }
//
//    override fun onPostClicked(post: Post) {
//        val action = MainFeedFragmentDirections.mainFeedFragment2ToPostDetialsFragment(post)
//        findNavController().navigate(action)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onUpVoteClicked(post: Post) {
//        viewModel.upVote(post)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onDownVoteClicked(post: Post) {
//        viewModel.downVote(post)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onMoreOptionClicked(imageView5: MaterialButton, postitem: Post) {
//        var resourceXml = R.menu.extra_option_menu
//        if (currentUser?.email != postitem.authorEmail) {
//            resourceXml = R.menu.extra_option_menu_not_owner
//        }
//        val popupMenu = PopupMenu(requireActivity(), imageView5)
//        popupMenu.menuInflater.inflate(resourceXml, popupMenu.menu)
//
//        // Set item click listener for the popup menu
//        popupMenu.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.item_save -> {
//                    // Handle Item 1 click
//                    // Add your code here
//                    true
//                }
//
//                R.id.item_delete -> {
//                    viewModel.deletePost(postitem)
//                    true
//                }
//
//                R.id.item_report -> {
//                    true
//                }
//
//                R.id.item_edit -> {
//                    val action =
//                        MainFeedFragmentDirections.mainFeedFragment2ToEditPostFragment(postitem)
//                    findNavController().navigate(action)
//                    true
//                }
//
//                else -> false
//            }
//        }
//        // Show the popup menu
//        popupMenu.show()
//    }
//
//    override fun onMoreOptionClicked(imageView5: MaterialButton, reply: Reply) {
//        TODO("Not yet implemented")
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onFeedOptionClicked() {
//        val bottomSheetBinding: BottomSheetFeedOptionsBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(requireContext()),
//            R.layout.bottom_sheet_feed_options,
//            null,
//            false
//        )
//        val bottomSheetDialog = BottomSheetDialog(requireContext())
//        viewModel.tags.forEach {
//            val chip: Chip = layoutInflater.inflate(R.layout.item_tag_filter_chip, null, false) as Chip
//            chip.text = it
//            chip.isChecked = viewModel.selectedTagFilters.value.contains(it)
//            bottomSheetBinding.tagsFilterChipgroup.addView(chip)
//        }
//        bottomSheetDialog.setContentView(bottomSheetBinding.root)
//        setSelectedChip(bottomSheetBinding)
//        val filters = mutableListOf<String>()
//        bottomSheetBinding.sortApplyButton.setOnClickListener {
//            when (bottomSheetBinding.sortTypesChipgroup.checkedChipId) {
//                R.id.newest_sort_chip -> {
//                    if(!viewModel.isSortedByNewest.value){
//                        viewModel.sortPostsByNewest()
//                    }
//                }
//                R.id.popular_sort_chip -> {
//                    if(viewModel.isSortedByNewest.value){
//                        viewModel.sortPostsByPopularity()
//                    }
//                }
//            }
//            bottomSheetBinding.tagsFilterChipgroup.children.forEach { chip->
//                if((chip as Chip).isChecked) {
//                    filters.add(chip.text.toString())
//                }
//            }
//            viewModel.updateTagFilters(filters)
//            Log.d("edrees", filters.joinToString(", "))
//            bottomSheetDialog.dismiss()
//        }
//        bottomSheetDialog.setOnShowListener {
//            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.bottomSheet)
//            bottomSheetBehavior.isHideable = false
//            val bottomSheetParent = bottomSheetBinding.bottomSheetParent
//            BottomSheetBehavior.from(bottomSheetParent.parent as View).peekHeight =
//                bottomSheetParent.height
//            bottomSheetBehavior.peekHeight = bottomSheetParent.height
//            bottomSheetParent.parent.requestLayout()
//        }
//        bottomSheetDialog.show()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun setSelectedChip(bottomSheetBinding: BottomSheetFeedOptionsBinding) {
//        if(viewModel.isSortedByNewest.value){
//            bottomSheetBinding.newestSortChip.isChecked = true
//        }else{
//            bottomSheetBinding.popularSortChip.isChecked = true
//        }
//    }
//
//    override fun onTagClicked(tag: Tag) {
//        val action = MainFeedFragmentDirections.mainFeedFragment2ToSearchFragment2(tag.label, true)
//        findNavController().navigate(action)
//    }
//
//    override fun onFileClicked(attachment: PostAttachment) {
//        val fileManager = FileManager(requireContext())
//        fileManager.downloadFile(attachment.url, attachment.name, MimeType.findByReadableType(attachment.type).value)
//    }
//
