package uni.zf.xinpian.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.data.AppConst.ARG_CATEGORY
import uni.zf.xinpian.data.AppConst.ARG_KEYWORD
import uni.zf.xinpian.data.AppConst.DEFAULT_CATEGORY_ID
import uni.zf.xinpian.json.model.RecommendItem

class SearchRecommendAdapter(private var items: List<RecommendItem> = listOf()) :
    RecyclerView.Adapter<SearchRecommendAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_search_reco, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        textView.text = items[position].title
        textView.setOnClickListener { toResult(textView.context, items[position]) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<RecommendItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    private fun toResult(context: Context, item: RecommendItem) {
        val intent = Intent(context, SearchResultActivity::class.java).apply {
            putExtra(ARG_CATEGORY, DEFAULT_CATEGORY_ID)
            putExtra(ARG_KEYWORD, item.title)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}