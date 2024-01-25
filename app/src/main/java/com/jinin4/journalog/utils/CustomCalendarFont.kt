package com.jinin4.journalog.utils

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomCalendarFont(private val newType: Typeface, private val newSize: Float) :
    MetricAffectingSpan() {
    override fun updateDrawState(ds: TextPaint) {
        ds.setTypeface(newType)
        ds.textSize = newSize
    }

    override fun updateMeasureState(paint: TextPaint) {
        paint.setTypeface(newType)
        paint.textSize = newSize
    }
}

