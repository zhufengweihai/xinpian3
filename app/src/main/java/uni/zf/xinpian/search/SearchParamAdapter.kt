package uni.zf.xinpian.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R
import uni.zf.xinpian.search.SearchType.CATEGORIES

class SearchParamAdapter(
    private var category: String? = null,
    private val searchType: SearchType,
    private var selectedPosition: Int,
    private val listener: SearchParamListener
) : RecyclerView.Adapter<SearchParamAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setCategory(category: String?) {
        this.category = category
        selectedPosition = 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_para, parent, false)
        if (searchType == CATEGORIES) view.layoutParams.width = parent.measuredWidth/5
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        textView.text = searchType.values(category)[position]
        textView.setBackgroundResource(if (position == selectedPosition) R.drawable.shape_search_para else 0)
        textView.setOnClickListener {
            if (selectedPosition != holder.bindingAdapterPosition) {
                selectedPosition = holder.bindingAdapterPosition
                listener.onSearchParam(searchType, selectedPosition)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = searchType.values(category).size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}