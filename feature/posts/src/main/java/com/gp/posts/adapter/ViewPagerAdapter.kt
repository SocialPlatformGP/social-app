package com.gp.posts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gp.posts.presentation.postsfeed.FeedFragment
import com.gp.posts.presentation.postsfeed.VipFeedFragment

private const val NUM_TABS = 2

class ViewPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount() = NUM_TABS

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> {
                // First Fragment
                VipFeedFragment()

            }

            else -> {
                // Second Fragment
                FeedFragment()
            }
        }

}