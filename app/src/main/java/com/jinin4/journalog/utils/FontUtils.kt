package com.jinin4.journalog.utils

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jinin4.journalog.R
import com.jinin4.journalog.dataStore
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.flow.first
//반정현 작성 - 24.01.24
object FontUtils {
    suspend fun getFontType(context: Context): Typeface? {
        val fontType = context.dataStore.data.first().fontType ?: "notosans"

        return when (fontType) {
            "nanumsquare" -> context.resources.getFont(R.font.nanumsquare)
            "nanumgothic" -> context.resources.getFont(R.font.nanumgothic)
            "nanummyeongjo" -> context.resources.getFont(R.font.nanummyeongjo)
            "nanumpen"-> context.resources.getFont(R.font.nanumpen)
            "nanumbarungothic"->context.resources.getFont(R.font.nanumbarungothic)
            else -> context.resources.getFont(R.font.nanumsquare)
        }
    }
    suspend fun getFontSize(context: Context): Float {
        val fontSize = context.dataStore.data.first().fontSize ?: 17
        return when {
            fontSize == 0 -> 17.toFloat()
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
                if (view.tag == "exclude") {
                    view.typeface = typeface
                    view.textSize = 17.toFloat()
                } else {
                    view.typeface = typeface
                    view.textSize = fontSize
                }
            }

        }
    }
}
