package uni.zf.xinpian.main

import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import uni.zf.xinpian.download.DownloadFragment
import uni.zf.xinpian.record.WatchRecordFragment
import uni.zf.xinpian.search.DiscoveryFragment

class MainFragmentStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    companion object {
        private const val HOME_FRAGMENT_POSITION = 0
        private const val DISCOVERY_FRAGMENT_POSITION = 1
        private const val DOWNLOAD_FRAGMENT_POSITION = 2
        private const val MY_FRAGMENT_POSITION = 3
        private const val TOTAL_COUNT = 4
    }

    override fun getItemCount() = TOTAL_COUNT

    @OptIn(UnstableApi::class)
    override fun createFragment(position: Int): Fragment = when (position) {
        HOME_FRAGMENT_POSITION -> HomeFragment() // 首页
        DISCOVERY_FRAGMENT_POSITION -> DiscoveryFragment() // 发现页
        DOWNLOAD_FRAGMENT_POSITION -> DownloadFragment() // 下载页
        MY_FRAGMENT_POSITION -> WatchRecordFragment() // 我的页
        else -> throw IllegalArgumentException("Invalid position: $position")
    }
}