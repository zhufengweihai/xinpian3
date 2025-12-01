package uni.zf.xinpian.player

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import uni.zf.xinpian.R

class BottomItemDecoration : RecyclerView.ItemDecoration {
    private val space: Int

    constructor(space: Int) {
        this.space = space
    }

    constructor(context: Context) {
        space = context.resources.getDimensionPixelSize(R.dimen.default_space)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = space
        outRect.bottom = space
    }
}