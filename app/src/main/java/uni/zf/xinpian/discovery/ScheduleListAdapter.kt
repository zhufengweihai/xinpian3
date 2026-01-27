package uni.zf.xinpian.discovery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ItemScheduleListBinding
import uni.zf.xinpian.json.model.DateMovieGroup
import uni.zf.xinpian.view.HorizontalItemDecoration

private val diffCallback = object : DiffUtil.ItemCallback<DateMovieGroup>() {
    override fun areItemsTheSame(oldItem: DateMovieGroup, newItem: DateMovieGroup): Boolean {
        return oldItem.date == newItem.date
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DateMovieGroup, newItem: DateMovieGroup): Boolean {
        return oldItem == newItem
    }
}

class ScheduleListAdapter() : PagingDataAdapter<DateMovieGroup, ScheduleListAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val rvSchedule: RecyclerView = itemView.findViewById(R.id.rv_schedule)
        fun bind(movieGroup: DateMovieGroup) {
            tvDate.text = "·" + movieGroup.date
            rvSchedule.layoutManager = LinearLayoutManager(itemView.context, HORIZONTAL, false)
            rvSchedule.adapter = ScheduleItemAdapter(movieGroup.movieList)
            val size = itemView.context.resources.getDimensionPixelSize(R.dimen.list_item_space)
            rvSchedule.addItemDecoration(HorizontalItemDecoration(size))
        }
    }
}