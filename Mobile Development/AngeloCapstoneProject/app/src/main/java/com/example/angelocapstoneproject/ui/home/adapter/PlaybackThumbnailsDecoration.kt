package com.example.angelocapstoneproject.ui.home.adapter

import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.angelocapstoneproject.domain.helper.toDp

class PlaybackThumbnailsDecoration(
    private val displayMetrics: DisplayMetrics,
    private val padding:Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            bottom = padding.toDp(displayMetrics)

            if (parent.getChildAdapterPosition(view) % 3 == 2) return

            right = padding.toDp(displayMetrics)
        }
    }


}