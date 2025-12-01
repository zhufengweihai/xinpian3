package uni.zf.xinpian.record

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.WatchRecord
import uni.zf.xinpian.utils.getSourceName
import uni.zf.xinpian.utils.loadImages

class WatchRecordAdapter(private val callBack: CallBack) : RecyclerView.Adapter<WatchRecordAdapter.ViewHolder>() {

    private var watchList: List<WatchRecord> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setWatchList(watchList: List<WatchRecord>) {
        this.watchList = watchList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_record, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val watchRecord = watchList[holder.bindingAdapterPosition]
        Glide.with(holder.imageView).load(watchRecord.image).into(holder.imageView)
        holder.titleView.text = watchRecord.name
        holder.statusView.text = watchRecord.status
        holder.percentView.text = holder.percentView.resources.getString(R.string.watch_to, watchRecord.percent)
        holder.deleteButton.setOnClickListener { callBack.deleteRecord(watchRecord) }
        holder.itemView.setOnClickListener { callBack.toPlay(watchRecord) }
        holder.episodeView.text = watchRecord.currentEpisode
        //holder.sourceView.text = getSourceName(watchRecord.vodId)
    }

    override fun getItemCount(): Int = watchList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val titleView: TextView = itemView.findViewById(R.id.title_view)
        val statusView: TextView = itemView.findViewById(R.id.status_view)
        val percentView: TextView = itemView.findViewById(R.id.percent_view)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        val episodeView: TextView = itemView.findViewById(R.id.episode_view)
        //val sourceView: TextView = itemView.findViewById(R.id.source_view)

        init {
            itemView.findViewById<ViewGroup>(R.id.cover_view).clipToOutline = true
        }
    }

    interface CallBack {
        fun deleteRecord(watchRecord: WatchRecord)
        fun toPlay(watchRecord: WatchRecord)
    }
}