package uni.zf.xinpian.play.download

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.SourceGroup

class SourceSelectAdapter(
    private val onSourceSelected: (Int) -> Unit
) : RecyclerView.Adapter<SourceSelectAdapter.ViewHolder>() {

    private var sources: List<SourceGroup> = emptyList()
    var selectedPosition = 0
        private set

    @SuppressLint("NotifyDataSetChanged")
    fun updateSources(newSources: List<SourceGroup>, selected: Int = 0) {
        sources = newSources
        selectedPosition = selected
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val source = sources[position]
        holder.textView.text = source.name
        holder.textView.isSelected = position == selectedPosition
        holder.textView.setBackgroundResource(
            if (position == selectedPosition) R.drawable.shape_episode_selected else R.drawable.shape_episode_normal
        )
        holder.itemView.setOnClickListener {
            val prev = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(prev)
            notifyItemChanged(selectedPosition)
            onSourceSelected(selectedPosition)
        }
    }

    override fun getItemCount() = sources.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_source)
    }
}
