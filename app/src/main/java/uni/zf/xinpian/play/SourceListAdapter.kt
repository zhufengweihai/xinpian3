package uni.zf.xinpian.play

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.SourceGroup

@SuppressLint("NotifyDataSetChanged")
open class SourceListAdapter(
    private val listener: SourceChangeListener,
    private var sources: List<SourceGroup> = emptyList(),
    private var currentSource: Int = 0,
) : RecyclerView.Adapter<SourceListAdapter.ViewHolder>() {

    fun updateSources(currentSource: Int = 0) {
        this.currentSource = currentSource
        notifyDataSetChanged()
    }

    fun updateSources(sources: List<SourceGroup>, currentSource: Int = 0) {
        this.sources = sources
        this.currentSource = currentSource
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sources[position].name, isSelected(position))
        holder.itemView.setOnClickListener { onItemClick(holder) }
    }

    protected open fun onItemClick(holder: ViewHolder) {
        val position = holder.bindingAdapterPosition
        if (position == currentSource) return
        currentSource = position
        listener.onSource(currentSource)
        notifyDataSetChanged()
    }

    open fun isSelected(position: Int) = position == currentSource

    override fun getItemCount(): Int = sources.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSource: TextView = itemView.findViewById(R.id.tv_source)

        fun bind(title: String, isSelected: Boolean) {
            tvSource.text = title
            val d = if (isSelected) R.drawable.shape_episode_pressed else R.drawable.shape_episode_unpressed
            tvSource.setBackgroundResource(d)
        }
    }
}