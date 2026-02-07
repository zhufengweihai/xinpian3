package uni.zf.xinpian.shorts

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView.switchTargetView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ActivityShortPlayBinding
import uni.zf.xinpian.play.PlayerFactory

class ShortPlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShortPlayBinding
    private val viewModel: ShortPlayViewModel by viewModels()
    private lateinit var player: ExoPlayer
    private lateinit var adapter: ShortPlayAdapter
    private var currentPlayingPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShortPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
    }

    private fun init() {
        player = PlayerFactory.createPlayer(this)
        adapter = ShortPlayAdapter(player)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                playVideo(position)
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

        lifecycleScope.launch {
            viewModel.getShortVideo()?.let {
                adapter.updateShortVideo(it)
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun playVideo(position: Int) {
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        val oldHolder = recyclerView.findViewHolderForAdapterPosition(currentPlayingPosition)
        if (viewHolder is ShortPlayAdapter.VideoViewHolder) {
            if (oldHolder is ShortPlayAdapter.VideoViewHolder) {
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

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }
}