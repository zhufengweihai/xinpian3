package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ItemScheduleBinding
import uni.zf.xinpian.json.model.DateMovieGroup

class ScheduleListAdapter() : RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>() {
    private val items: List<DateMovieGroup> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val rvSchedule: RecyclerView = itemView.findViewById(R.id.rv_schedule)
        fun bind(movieGroup: DateMovieGroup) {
            tvDate.text = movieGroup.date

        }
    }
}