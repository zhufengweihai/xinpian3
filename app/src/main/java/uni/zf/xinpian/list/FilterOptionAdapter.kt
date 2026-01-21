package uni.zf.xinpian.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.FilterGroup

class FilterOptionAdapter(
    private var filterGroup: FilterGroup = FilterGroup("", listOf()),
    private val listener: FilterOptionListener
) : RecyclerView.Adapter<FilterOptionAdapter.ViewHolder>() {
    private var selectedPosition = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setOptions(filterGroup: FilterGroup) {
        this.filterGroup = filterGroup
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_para, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        textView.text = filterGroup.options[position].name
        textView.setBackgroundResource(if (position == selectedPosition) R.drawable.shape_search_para else 0)
        textView.setOnClickListener {
            if (selectedPosition != holder.bindingAdapterPosition) {
                selectedPosition = holder.bindingAdapterPosition
                listener.onFilterOption(filterGroup.key,filterGroup.options[selectedPosition])
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = filterGroup.options.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}