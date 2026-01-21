package uni.zf.xinpian.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.WatchHistory

class WatchHistoryAdapter(private val callBack: CallBack) : RecyclerView.Adapter<WatchHistoryAdapter.ViewHolder>() {

    private var watchList: List<WatchHistory> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setWatchList(watchList: List<WatchHistory>) {
        this.watchList = watchList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_record, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val watchHistory = watchList[holder.bindingAdapterPosition]
        Glide.with(holder.imageView).load(watchHistory.image).into(holder.imageView)
        holder.titleView.text = watchHistory.title
        holder.statusView.text = watchHistory.status
        holder.percentView.text = holder.percentView.resources.getString(R.string.watch_to, watchHistory.percent)
        holder.deleteButton.setOnClickListener { callBack.deleteHistory(watchHistory) }
        holder.itemView.setOnClickListener { callBack.toPlay(watchHistory) }
        holder.episodeView.text = watchHistory.currentEpisode
    }

    override fun getItemCount(): Int = watchList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val titleView: TextView = itemView.findViewById(R.id.title_view)
        val statusView: TextView = itemView.findViewById(R.id.status_view)
        val percentView: TextView = itemView.findViewById(R.id.percent_view)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        val episodeView: TextView = itemView.findViewById(R.id.episode_view)

        init {
            itemView.findViewById<ViewGroup>(R.id.cover_view).clipToOutline = true
        }
    }

    interface CallBack {
        fun deleteHistory(watchHistory: WatchHistory)
        fun toPlay(watchHistory: WatchHistory)
    }
}