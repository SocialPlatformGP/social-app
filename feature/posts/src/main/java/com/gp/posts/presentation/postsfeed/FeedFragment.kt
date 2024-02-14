package com.gp.posts.presentation.postsfeed

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.posts.R
import com.gp.posts.databinding.BottomSheetFeedOptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private val viewModel: FeedPostViewModel by viewModels()
    private val currentUser = Firebase.auth.currentUser





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

//    override fun onMoreOptionClicked(imageView5: MaterialButton, reply: Reply) {
//        TODO("Not yet implemented")
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onFeedOptionClicked() {
        val bottomSheetBinding: BottomSheetFeedOptionsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.bottom_sheet_feed_options,
            null,
            false
        )
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        viewModel.tags.forEach {
            val chip: Chip = layoutInflater.inflate(R.layout.item_tag_filter_chip, null, false) as Chip
            chip.text = it
            chip.isChecked = viewModel.selectedTagFilters.value.contains(it)
            bottomSheetBinding.tagsFilterChipgroup.addView(chip)
        }
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        setSelectedChip(bottomSheetBinding)
        val filters = mutableListOf<String>()
        bottomSheetBinding.sortApplyButton.setOnClickListener {
            when (bottomSheetBinding.sortTypesChipgroup.checkedChipId) {
                R.id.newest_sort_chip -> {
                    if(!viewModel.isSortedByNewest.value){
                        viewModel.sortPostsByNewest()
                    }
                }
                R.id.popular_sort_chip -> {
                    if(viewModel.isSortedByNewest.value){
                        viewModel.sortPostsByPopularity()
                    }
                }
            }
            bottomSheetBinding.tagsFilterChipgroup.children.forEach { chip->
                if((chip as Chip).isChecked) {
                    filters.add(chip.text.toString())
                }
            }
            viewModel.updateTagFilters(filters)
            Log.d("edrees", filters.joinToString(", "))
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setOnShowListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.bottomSheet)
            bottomSheetBehavior.isHideable = false
            val bottomSheetParent = bottomSheetBinding.bottomSheetParent
            BottomSheetBehavior.from(bottomSheetParent.parent as View).peekHeight =
                bottomSheetParent.height
            bottomSheetBehavior.peekHeight = bottomSheetParent.height
            bottomSheetParent.parent.requestLayout()
        }
        bottomSheetDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSelectedChip(bottomSheetBinding: BottomSheetFeedOptionsBinding) {
        if(viewModel.isSortedByNewest.value){
            bottomSheetBinding.newestSortChip.isChecked = true
        }else{
            bottomSheetBinding.popularSortChip.isChecked = true
        }
    }

//    override fun onTagClicked(tag: Tag) {
//        val action = MainFeedFragmentDirections.mainFeedFragment2ToSearchFragment2(tag.label, true)
//        findNavController().navigate(action)
//    }
//
//    override fun onFileClicked(attachment: PostAttachment) {
//        val fileManager = FileManager(requireContext())
//        fileManager.downloadFile(attachment.url, attachment.name, MimeType.findByReadableType(attachment.type).value)
//    }


}