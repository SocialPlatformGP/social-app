package com.gp.posts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gp.posts.presentation.postsfeed.FeedFragment

private const val NUM_TABS = 2

class ViewPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount() = NUM_TABS

    override fun createFragment(position: Int) =
        when (position) {
            0 -> {
                // First Fragment
                FeedFragment()
            }

            else -> {
                // First Fragment
                FeedFragment()
            }
        }

}