package com.jinin4.journalog

import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape


// 이상원 - 24.01.23
object MemoRightColorSetting {

    fun changeRightColor(color: Int, layerDrawable: LayerDrawable): LayerDrawable {
        val rightPart = ShapeDrawable(RoundRectShape(floatArrayOf(30f, 30f, 30f, 30f, 30f, 30f, 30f, 30f), null, null))
        rightPart.paint.color = color
        layerDrawable.setDrawableByLayerId(R.id.rightPart, rightPart)
        return layerDrawable
    }

//    fun changeLeftColor(color: Int, layerDrawable: LayerDrawable): LayerDrawable {
//        val leftPart = ShapeDrawable(RoundRectShape(floatArrayOf(10f, 10f, 0f, 0f, 10f, 10f, 0f, 0f), null, null))
//        leftPart.paint.color = color
//        layerDrawable.setDrawableByLayerId(R.id.leftPart, leftPart)
//        return layerDrawable
//    }
}