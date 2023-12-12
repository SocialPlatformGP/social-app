package com.gp.posts.presentation.postsfeed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.gp.posts.R
import com.gp.posts.adapter.ViewPagerAdapter
import com.gp.posts.databinding.FragmentMainFeedBinding

val taps = arrayOf(
    "VIP",
    "All"
)
class MainFeedFragment : Fragment() {
lateinit var  binding :FragmentMainFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_main_feed,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = taps[position]
        }.attach()
    }

}