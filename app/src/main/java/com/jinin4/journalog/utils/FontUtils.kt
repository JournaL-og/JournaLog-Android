package com.jinin4.journalog.utils

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import kotlinx.coroutines.flow.first
//반정현 작성 - 24.01.24
object FontUtils {
    suspend fun getFontType(context: Context): Typeface? {
        val fontType = context.dataStore.data.first().fontType ?: "notosans"

        return when (fontType) {
            "notosans" -> context.resources.getFont(R.font.notosans)
            "nanumgothic" -> context.resources.getFont(R.font.nanumgothic)
            "nanummyeongjo" -> context.resources.getFont(R.font.nanummyeongjo)
            else -> context.resources.getFont(R.font.notosans)
        }
    }
    suspend fun getFontSize(context: Context): Float {
        val fontSize = context.dataStore.data.first().fontSize ?: 16
        return when {
            fontSize <= 12 -> 12.toFloat()
            fontSize >= 30 -> 30.toFloat()
            else -> fontSize.toFloat()
        }
    }

    fun applyFont(view: View, typeface: Typeface?, fontSize: Float) {
        when (view) {
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyFont(view.getChildAt(i), typeface, fontSize)
                }
            }
            is TextView -> {
                view.typeface = typeface
                view.textSize = fontSize
            }
        }
    }
}
