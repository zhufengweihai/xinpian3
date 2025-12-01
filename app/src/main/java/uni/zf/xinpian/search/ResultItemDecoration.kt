package uni.zf.xinpian.search

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ResultItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)

        with(outRect) {
            right = space
            bottom = space * 2
            left = if (position % 2 == 0) space else 0
            top = if (position < 2) space else 0
        }
    }
}