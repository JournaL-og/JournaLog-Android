package com.jinin4.journalog.calendar

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

//이상원 - 24.01.19
class EventDecorator(private val radius: Float, private val color: Int, dates: Collection<CalendarDay>)
    : DayViewDecorator {

    private val datesSet = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return datesSet.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CircleSpan(radius, color))
//        view.addSpan(DotSpan(5f, color))
    }

    inner class CircleSpan(private val radius: Float, private val color: Int) : LineBackgroundSpan {

        override fun drawBackground(
            canvas: Canvas,
            paint: Paint,
            left: Int,
            right: Int,
            top: Int,
            baseline: Int,
            bottom: Int,
            text: CharSequence,
            start: Int,
            end: Int,
            lnum: Int
        ) {
            val oldColor = paint.color
            paint.color = color
            val x = (left + right) / 2
            val y = (top + bottom) / 0.7
            canvas.drawCircle(x.toFloat(), y.toFloat(), radius, paint)
            paint.color = oldColor
        }
    }
}