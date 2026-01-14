package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.databinding.ItemScheduleDateBinding
import uni.zf.xinpian.databinding.ItemScheduleHeaderYearBinding
import uni.zf.xinpian.databinding.ItemScheduleMovieBinding

class ScheduleAdapter(private val items: List<ScheduleItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_YEAR = 0
        private const val TYPE_DATE = 1
        private const val TYPE_MOVIE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ScheduleItem.Year -> TYPE_YEAR
            is ScheduleItem.Date -> TYPE_DATE
            is ScheduleItem.Movie -> TYPE_MOVIE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_YEAR -> YearViewHolder(ItemScheduleHeaderYearBinding.inflate(inflater, parent, false))
            TYPE_DATE -> DateViewHolder(ItemScheduleDateBinding.inflate(inflater, parent, false))
            TYPE_MOVIE -> MovieViewHolder(ItemScheduleMovieBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ScheduleItem.Year -> (holder as YearViewHolder).binding.tvYear.text = item.year
            is ScheduleItem.Date -> (holder as DateViewHolder).binding.tvDate.text = item.date
            is ScheduleItem.Movie -> {
                val movieHolder = holder as MovieViewHolder
                movieHolder.binding.apply {
                    tvTitle.text = item.title
                    Glide.with(root.context).load(item.posterUrl).into(ivPoster)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class YearViewHolder(val binding: ItemScheduleHeaderYearBinding) : RecyclerView.ViewHolder(binding.root)
    class DateViewHolder(val binding: ItemScheduleDateBinding) : RecyclerView.ViewHolder(binding.root)
    class MovieViewHolder(val binding: ItemScheduleMovieBinding) : RecyclerView.ViewHolder(binding.root)
}