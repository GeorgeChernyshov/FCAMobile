package com.fca.fcamobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fca.fcamobile.databinding.FragmentGraphContainerBinding
import com.fca.fcamobile.ui.adapters.GraphContainerPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class GraphContainerFragment : Fragment() {

    private lateinit var binding: FragmentGraphContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGraphContainerBinding.inflate(inflater, container, false)

        with (binding) {
            val pagerAdapter = GraphContainerPagerAdapter(requireActivity())
            viewPager.offscreenPageLimit = pagerAdapter.itemCount
            viewPager.isUserInputEnabled = false
            viewPager.adapter = pagerAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = context?.getString(pagerAdapter.getItemNameId(position))
            }.attach()

            return root
        }
    }
}