package uni.zf.xinpian.short

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView.switchTargetView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.databinding.FragmentShortVideoBinding
import uni.zf.xinpian.player.PlayerFactory

class ShortVideoFragment : Fragment() {
    private lateinit var binding: FragmentShortVideoBinding
    private val viewModel: ShortVideoViewModel by viewModels()
    private lateinit var player: ExoPlayer
    private var isDataLoaded = false
    private var currentPlayingPosition = -1

    @OptIn(UnstableApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShortVideoBinding.inflate(inflater, container, false)
        player = PlayerFactory.createPlayer(requireContext())
        val adapter = ShortVideoAdapter(player)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                playVideo(position)
            }
        })

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (!isDataLoaded && isVisible) {
                    lifecycleScope.launch {
                        viewModel.dataFlow.collectLatest { adapter.submitData(it) }
                    }
                    isDataLoaded = true
                }
            }
        })

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    val nextPosition = binding.viewPager.currentItem + 1
                    if (nextPosition < binding.viewPager.adapter!!.itemCount) {
                        binding.viewPager.setCurrentItem(nextPosition, true)
                    }
                }
            }
        })

        return binding.root
    }

    @OptIn(UnstableApi::class)
    private fun playVideo(position: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        val oldHolder = recyclerView.findViewHolderForAdapterPosition(currentPlayingPosition)
        if (viewHolder is ShortVideoAdapter.VideoViewHolder) {
            if (oldHolder is ShortVideoAdapter.VideoViewHolder) {
                switchTargetView(player, oldHolder.playerView, viewHolder.playerView)
            } else {
                switchTargetView(player, null, viewHolder.playerView)
            }
            player.seekTo(position, 0)
            viewHolder.playerView.showController()
            player.prepare()
            player.play()
            currentPlayingPosition = position
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }
}
