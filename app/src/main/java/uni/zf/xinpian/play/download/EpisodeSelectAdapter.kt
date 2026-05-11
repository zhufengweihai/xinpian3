package uni.zf.xinpian.play.download

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.SourceItem

class EpisodeSelectAdapter : RecyclerView.Adapter<EpisodeSelectAdapter.ViewHolder>() {

    private var items: List<SourceItem> = emptyList()
    private val selectedPositions = mutableSetOf<Int>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<SourceItem>) {
        items = newItems
        selectedPositions.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        if (selectedPositions.size == items.size) {
            selectedPositions.clear()
        } else {
            selectedPositions.addAll(items.indices)
        }
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<SourceItem> {
        return selectedPositions.sorted().map { items[it] }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.sourceName
        val isSelected = selectedPositions.contains(position)
        holder.textView.isSelected = isSelected
        holder.textView.setBackgroundResource(
            if (isSelected) R.drawable.shape_episode_selected else R.drawable.shape_episode_normal
        )
        holder.itemView.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (selectedPositions.contains(pos)) {
                selectedPositions.remove(pos)
            } else {
                selectedPositions.add(pos)
            }
            notifyItemChanged(pos)
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_episode)
    }
}
