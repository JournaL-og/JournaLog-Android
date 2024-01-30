package com.jinin4.journalog.calendar

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams


// 이상원 - 24.01.22
class CustomLayoutManager(context: Context) : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler!!)
            return
        }

        // 모든 아이템 제거
        detachAndScrapAttachedViews(recycler!!)

        val parentWidth = width
        val parentHeight = height
        val childWidth = parentWidth / 2
        val childHeight = parentHeight / 2

        // 첫 번째 아이템 (왼쪽 상단)
        val firstView = recycler.getViewForPosition(0)
        addView(firstView)
        measureChildWithMargins(firstView, 0, 0)
        layoutDecoratedWithMargins(
            firstView,
            0,
            0,
            childWidth,
            parentHeight
        )

        // 두 번째 아이템 (오른쪽 상단)
        val secondView = recycler.getViewForPosition(1)
        addView(secondView)
        measureChildWithMargins(secondView, 0, 0)
        layoutDecoratedWithMargins(
            secondView,
            childWidth,
            0,
            parentWidth,
            childHeight
        )

        // 세 번째 아이템 (오른쪽 하단)
        val thirdView = recycler.getViewForPosition(2)
        addView(thirdView)
        measureChildWithMargins(thirdView, 0, 0)
        layoutDecoratedWithMargins(
            thirdView,
            childWidth,
            childHeight,
            parentWidth,
            parentHeight
        )
    }
}