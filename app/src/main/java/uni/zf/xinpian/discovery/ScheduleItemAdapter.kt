package uni.zf.xinpian.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.databinding.ItemScheduleVideoBinding
import uni.zf.xinpian.discovery.ScheduleItemAdapter.ViewHolder
import uni.zf.xinpian.json.model.MovieDetail
import uni.zf.xinpian.utils.ImageLoadUtil.loadImagesWithDomain

class ScheduleItemAdapter(private val items: List<MovieDetail>) : RecyclerView.Adapter<ViewHolder>
    () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleVideoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivVideo: ImageView = itemView.findViewById(R.id.iv_video)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        fun bind(movieDetail: MovieDetail) {
            loadImagesWithDomain(ivVideo, movieDetail.posterPath)
            tvName.text = movieDetail.title
        }
    }
}