package uni.zf.xinpian.player

import android.annotation.SuppressLint
import android.view.View
import uni.zf.xinpian.data.model.Episode

class DownloadEpisodesAdapter(
    private val listener: EpisodeChangeListener,
    private val episodes: List<Episode> = emptyList()
) :
    EpisodeListAdapter(listener, episodes, horizontal = false) {
    private var downloadList = emptyList<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setDownloadList(downloadList: List<String>) {
        this.downloadList = downloadList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val selected = downloadList.contains(episodes[holder.bindingAdapterPosition].url)
        holder.downloadLabelView.visibility = if (selected) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { listener.onDownload(holder.bindingAdapterPosition) }
    }

    override fun isSelected(position: Int) = false
}