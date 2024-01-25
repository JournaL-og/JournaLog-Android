package com.jinin4.journalog.utils

import android.graphics.Typeface
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CustomFontCalendarDecorator(private val typeface: Typeface?, private val size: Float) :
    DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay) = true

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomCalendarFont(typeface!!, size))
    }
}