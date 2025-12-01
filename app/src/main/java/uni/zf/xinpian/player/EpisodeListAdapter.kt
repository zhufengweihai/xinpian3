package uni.zf.xinpian.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.Episode

@SuppressLint("NotifyDataSetChanged")
open class EpisodeListAdapter(
    private val listener: EpisodeChangeListener,
    private var episodes: List<Episode> = emptyList(),
    private var currentPos: Int = 0,
    private val horizontal: Boolean = true
) : RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>() {

    fun updateEpisodes(currentPos: Int = 0) {
        this.currentPos = currentPos
        notifyDataSetChanged()
    }

    fun updateEpisodes(episodes: List<Episode>, currentPos: Int = 0) {
        this.episodes = episodes
        this.currentPos = currentPos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return ViewHolder(view, horizontal)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(episodes[position].title, isSelected(position))
        holder.itemView.setOnClickListener { onItemClick(holder) }
    }

    protected open fun onItemClick(holder: ViewHolder) {
        val position = holder.bindingAdapterPosition
        if (position == currentPos) return
        currentPos = position
        listener.onEpisode(currentPos)
        notifyDataSetChanged()
    }

    open fun isSelected(position: Int) = position == currentPos

    override fun getItemCount(): Int = episodes.size

    class ViewHolder(itemView: View, horizontal: Boolean) : RecyclerView.ViewHolder(itemView) {
        private val episodeView: TextView = itemView.findViewById(R.id.episode_view)
        val downloadLabelView: View = itemView.findViewById(R.id.download_label_view)

        init {
            if (!horizontal) {
                itemView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    itemView.layoutParams.height
                )
            }
        }

        fun bind(title: String, isSelected: Boolean) {
            episodeView.text = title
            val d = if (isSelected) R.drawable.shape_episode_pressed else R.drawable.shape_episode_unpressed
            episodeView.setBackgroundResource(d)
        }
    }
}