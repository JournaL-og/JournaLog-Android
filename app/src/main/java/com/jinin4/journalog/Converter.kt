package com.jinin4.journalog

import androidx.room.TypeConverter
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
// 이상원 24.01.22
class Converter {

    @TypeConverter
    fun fromCalendarDay(calendarDay: CalendarDay?): String? {
        return calendarDay?.date?.format(DateTimeFormatter.ISO_DATE)
    }

    @TypeConverter
    fun toCalendarDay(dateString: String?): CalendarDay? {
        return dateString?.let {
            val localDate = LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
            CalendarDay.from(localDate)
        }
    }
}