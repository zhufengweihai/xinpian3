package uni.zf.xinpian.series

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SeriesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        outRect.apply {
            right = space
            bottom = space * 2
            left = if (position % 3 == 0) space else 0
            top = if (position < 3) space else 0
        }
    }
}