package com.fca.fcamobile.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fca.fcamobile.R
import com.fca.fcamobile.ui.fragments.FilterFragment
import com.fca.fcamobile.ui.fragments.GraphFragment

class GraphContainerPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragmentTypes = listOf(
        FRAGMENT_FILTER,
        FRAGMENT_GRAPH
    )

    override fun getItemCount() = fragmentTypes.size

    override fun createFragment(position: Int): Fragment = when (position) {
        FRAGMENT_FILTER -> FilterFragment()
        else -> GraphFragment()
    }

    fun getItemNameId(position: Int) = when (position) {
        FRAGMENT_FILTER -> R.string.filter_fragment_label
        else -> R.string.graph_fragment_label
    }

    companion object {
        private const val FRAGMENT_FILTER = 0
        private const val FRAGMENT_GRAPH = 1
    }
}