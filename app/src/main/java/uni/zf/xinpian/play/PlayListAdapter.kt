package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.SourceGroup
import uni.zf.xinpian.json.model.SourceItem
import uni.zf.xinpian.player.EpisodeChangeListener

@SuppressLint("NotifyDataSetChanged")
open class PlayListAdapter(
    private val listener: EpisodeChangeListener,
    private var items: List<SourceItem> = emptyList(),
    private var currentItem: Int = 0,
) : RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {

    fun updateItems(currentItem: Int = 0) {
        this.currentItem = currentItem
        notifyDataSetChanged()
    }

    fun updateItems(items: List<SourceItem>, currentItem: Int = 0) {
        this.items = items
        this.currentItem = currentItem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position].sourceName, isSelected(position))
        holder.itemView.setOnClickListener { onItemClick(holder) }
    }

    protected open fun onItemClick(holder: ViewHolder) {
        val position = holder.bindingAdapterPosition
        if (position == currentItem) return
        currentItem = position
        listener.onEpisode(currentItem)
        notifyDataSetChanged()
    }

    open fun isSelected(position: Int) = position == currentItem

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSource: TextView = itemView.findViewById(R.id.tv_source)

        fun bind(title: String, isSelected: Boolean) {
            tvSource.text = title
            val d = if (isSelected) R.drawable.shape_episode_pressed else R.drawable.shape_episode_unpressed
            tvSource.setBackgroundResource(d)
        }
    }
}