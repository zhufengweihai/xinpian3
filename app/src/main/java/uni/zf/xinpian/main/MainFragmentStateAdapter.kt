package uni.zf.xinpian.main

import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import uni.zf.xinpian.list.ListFragment
import uni.zf.xinpian.short.ShortVideoFragment

class MainFragmentStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    companion object {
        private const val HOME_FRAGMENT_POSITION = 0
        private const val SHORT_FRAGMENT_POSITION = 1
        private const val LIST_FRAGMENT_POSITION = 2
        private const val DISCOVERY_FRAGMENT_POSITION = 3
        private const val TOTAL_COUNT = 4
    }

    override fun getItemCount() = TOTAL_COUNT

    @OptIn(UnstableApi::class)
    override fun createFragment(position: Int): Fragment = when (position) {
        HOME_FRAGMENT_POSITION -> HomeFragment()
        SHORT_FRAGMENT_POSITION -> ShortVideoFragment()
        LIST_FRAGMENT_POSITION -> ListFragment()
        DISCOVERY_FRAGMENT_POSITION -> ListFragment()
        else -> throw IllegalArgumentException("Invalid position: $position")
    }
}