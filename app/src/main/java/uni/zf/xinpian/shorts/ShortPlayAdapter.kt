package uni.zf.xinpian.shorts

import ShortVideoItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.ShortVideo

class ShortPlayAdapter(private val player: ExoPlayer) : RecyclerView.Adapter<ShortPlayAdapter.VideoViewHolder>() {
    private var shortVideo = ShortVideo()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_short_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(shortVideo.playList[position], position)
    }

    override fun getItemCount(): Int = shortVideo.playList.size
    fun updateShortVideo(shortVideo: ShortVideo) {
        this.shortVideo = shortVideo
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView = itemView.findViewById(R.id.player_view)
        val titleView: TextView = playerView.findViewById(R.id.title)
        val tvEpisode: TextView = playerView.findViewById(R.id.tv_episode)
        val countView: TextView = playerView.findViewById(R.id.tv_count)

        init {
            playerView.findViewById<View>(R.id.tv_play).visibility = View.GONE
        }

        @OptIn(UnstableApi::class)
        fun bind(item: ShortVideoItem, position: Int) {
            titleView.text = shortVideo.title
            tvEpisode.text = item.episodeTitle
            tvEpisode.visibility = View.VISIBLE
            countView.text = "共${shortVideo.episodesCount}集"
            if (player.mediaItemCount <= position + 1) player.addMediaItem(MediaItem.fromUri(item.episodeUrl))
        }
    }
}
