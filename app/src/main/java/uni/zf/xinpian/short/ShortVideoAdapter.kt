package uni.zf.xinpian.short

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.ShortVideo
import uni.zf.xinpian.short.ShortVideoAdapter.VideoViewHolder

private val diffCallback = object : DiffUtil.ItemCallback<ShortVideo>() {
    override fun areItemsTheSame(oldItem: ShortVideo, newItem: ShortVideo): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ShortVideo, newItem: ShortVideo): Boolean {
        return oldItem == newItem
    }
}

class ShortVideoAdapter(private val player: ExoPlayer) : PagingDataAdapter<ShortVideo, VideoViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_short_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position) }
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView = itemView.findViewById(R.id.player_view)
        val titleView: TextView = playerView.findViewById(R.id.title)

        @OptIn(UnstableApi::class)
        fun bind(video: ShortVideo, position: Int) {
            titleView.text = video.title
            if (player.mediaItemCount <= position + 1) player.addMediaItem(MediaItem.fromUri(video.url))
        }
    }
}
