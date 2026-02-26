package uni.zf.xinpian.shorts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView.switchTargetView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.databinding.FragmentShortListBinding
import uni.zf.xinpian.play.PlayerFactory

class ShortListFragment : Fragment() {
    private lateinit var binding: FragmentShortListBinding
    private val viewModel: ShortListViewModel by viewModels()
    private lateinit var player: ExoPlayer
    private var isDataLoaded = false
    private var currentPlayingPosition = -1
    private lateinit var adapter: ShortVideoListAdapter

    @OptIn(UnstableApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShortListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayer()
        initUI()
    }

    private fun initUI(){
        adapter = ShortVideoListAdapter(player)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                playVideo(position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
        if (!player.isPlaying) player.play()
    }

    private fun initPlayer(){
        player = PlayerFactory.createPlayer(requireContext(), false)
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
    }
    private fun loadData() {
        if (!isDataLoaded){
            lifecycleScope.launch {
                viewModel.dataFlow.collectLatest { adapter.submitData(it) }
            }
            isDataLoaded = true
        }
    }
    @OptIn(UnstableApi::class)
    private fun playVideo(position: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        val oldHolder = recyclerView.findViewHolderForAdapterPosition(currentPlayingPosition)
        if (viewHolder is ShortVideoListAdapter.VideoViewHolder) {
            if (oldHolder is ShortVideoListAdapter.VideoViewHolder) {
                switchTargetView(player, oldHolder.playerView, viewHolder.playerView)
            } else {
                switchTargetView(player, null, viewHolder.playerView)
            }
            player.seekTo(position, 0)
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
