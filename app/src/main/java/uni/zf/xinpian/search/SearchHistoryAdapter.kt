package uni.zf.xinpian.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.data.model.SearchHistory

class SearchHistoryAdapter(private val listener: SearchHistoryListener) :
    RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    private var historyList: List<SearchHistory> = emptyList()
    private var deleteMode = false

    @SuppressLint("NotifyDataSetChanged")
    fun setHistories(historyList: List<SearchHistory>) {
        this.historyList = historyList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDeleteMode(deleteMode: Boolean) {
        if (this.deleteMode != deleteMode) {
            this.deleteMode = deleteMode
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        holder.keywordView.text = history.keyword
        holder.deleteView.visibility = if (deleteMode) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            listener.onSearchHistory(history.keyword, deleteMode)
        }
    }

    override fun getItemCount(): Int = historyList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keywordView: TextView = itemView.findViewById(R.id.keyword_view)
        val deleteView: TextView = itemView.findViewById(R.id.to_del_view)
    }
}