package uni.zf.xinpian.category

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import uni.zf.xinpian.R
import uni.zf.xinpian.json.model.CustomTag

class CustomTagAdapter(private var tagList: List<CustomTag> = listOf()) : Adapter<CustomTagAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateCustomTagList(tagList: List<CustomTag>) {
        this.tagList = tagList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_tag, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount() = tagList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTag: TextView = itemView.findViewById(R.id.tv_tag)

        fun bind(customTag: CustomTag) {
            tvTag.text = customTag.title
            tvTag.setOnClickListener {
                tvTag.context.startActivity(
                    Intent(
                        tvTag.context,
                        CustomTagActivity::class.java
                    ).apply {

                    })
            }
        }
    }
}