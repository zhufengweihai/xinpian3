package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ItemScheduleFilterBinding
import uni.zf.xinpian.json.model.WeekRankOption

class ScheduleFilterAdapter(
    private var rankOptions: List<WeekRankOption> = listOf(),
    private val onFilterSelected: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<ScheduleFilterAdapter.ViewHolder>() {

    private var selectedPosition = 0

    class ViewHolder(val binding: ItemScheduleFilterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filter = rankOptions[position]
        holder.binding.tvFilterName.apply {
            text = filter.name
            if (position == selectedPosition) {
                setBackgroundResource(R.drawable.shape_tag_selected)
            } else {
                setBackgroundResource(R.drawable.shape_tag_unselected)
            }
            
            setOnClickListener {
                if (selectedPosition != holder.adapterPosition) {
                    val oldPosition = selectedPosition
                    selectedPosition = holder.adapterPosition
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                    onFilterSelected?.invoke(selectedPosition)
                }
            }
        }
    }

    override fun getItemCount() = rankOptions.size
}