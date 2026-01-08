package uni.zf.xinpian.short

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import uni.zf.xinpian.databinding.FragmentShortVideoBinding

class ShortVideoFragment : Fragment() {

    private lateinit var binding: FragmentShortVideoBinding
    private lateinit var adapter: ShortVideoAdapter
    private var player: ExoPlayer? = null

    private val videos = listOf(
        "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
        "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
        "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
        "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698225.mp4",
        "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShortVideoBinding.inflate(inflater, container, false)
        adapter = ShortVideoAdapter(videos)
        binding.viewPager.adapter = adapter

        player = ExoPlayer.Builder(requireContext()).build()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                playVideo(position)
            }
        })

        binding.viewPager.post { playVideo(0) }

        return binding.root
    }

    private fun playVideo(position: Int) {
        val viewHolder = (binding.viewPager.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(position)
        if (viewHolder is ShortVideoAdapter.VideoViewHolder) {
            viewHolder.playerView.player = player
            val mediaItem = MediaItem.fromUri(videos[position])
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }
}
