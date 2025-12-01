package uni.zf.xinpian.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.view.BannerView
import uni.zf.xinpian.data.model.RecData
import uni.zf.xinpian.data.model.Video
import uni.zf.xinpian.view.SpaceItemDecoration

class RecDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var topRecoList: List<Video> = emptyList()
    private var lists: Array<List<Video>> = emptyArray()
    private var titles: Array<String> = emptyArray()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(recData: RecData, titles: Array<String>) {
        this.topRecoList = recData.top_reco_list
        this.lists = recData.recLists
        this.titles = titles
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_BANNER else VIEW_TYPE_LIST
    }

    override fun getItemCount(): Int {
        return titles.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_BANNER -> {
                val view = inflater.inflate(R.layout.item_rec_banners, parent, false)
                BannerViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.view_rec_list, parent, false)
                ListViewHolder(view, parent.context)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BannerViewHolder -> {
                val itemView: BannerView = holder.itemView as BannerView
                itemView.setVideoList(topRecoList, false)
            }
            is ListViewHolder -> {
                val pos = position - 1
                holder.bind(titles[pos], lists[pos])
            }
        }
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ListViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private val recTitleView: TextView = itemView.findViewById(R.id.rec_title_view)
        private val recListView: RecyclerView = itemView.findViewById(R.id.rec_list_view)

        init {
            recListView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recListView.addItemDecoration(SpaceItemDecoration(context))
        }

        fun bind(title: String, videoList: List<Video>) {
            recTitleView.text = title
            recListView.adapter = RecVideoListAdapter(videoList, true)
        }
    }

    companion object {
        private const val VIEW_TYPE_BANNER = 0
        private const val VIEW_TYPE_LIST = 1
    }
}