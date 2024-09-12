package com.hblab.alarmapp.utils

import android.content.Context
import android.content.Intent
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.utils.Constants.Companion.FRIDAY
import com.hblab.alarmapp.utils.Constants.Companion.MONDAY
import com.hblab.alarmapp.utils.Constants.Companion.SATURDAY
import com.hblab.alarmapp.utils.Constants.Companion.SUNDAY
import com.hblab.alarmapp.utils.Constants.Companion.THURSDAY
import com.hblab.alarmapp.utils.Constants.Companion.TUESDAY
import com.hblab.alarmapp.utils.Constants.Companion.WEDNESDAY
import java.text.DateFormat
import java.util.*


private const val TAG = "calendarUtil"

class CalendarUtil {

    fun setCalendar(alarmItem: AlarmItem): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarmItem.hour)
            set(Calendar.MINUTE, alarmItem.minute)
            set(Calendar.SECOND, 0)
        }
    }
    fun setCalendar(hour: Int, minute: Int): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
    }

    fun formatCalendarTime(alarmItem: AlarmItem): String =
        DateFormat.getTimeInstance(DateFormat.SHORT).format(setCalendar(alarmItem).time)

    fun getAlarmDay(context: Context, alarmDate: Calendar) =
        if (isTimePassed(alarmDate)) { // if alarm time already passed
            // add one day to the calendar
            alarmDate.add(Calendar.DATE, 1)
            // return that day
            context.getString(R.string.tomorrow)
        } else context.getString(R.string.today) // the time is not passed, set nextAlarmDay variable to today


    private fun isTimePassed(alarmDate: Calendar) =
        alarmDate.before(Calendar.getInstance())


    // check if there is scheduled alarm for today
    fun isTodayScheduled(intent: Intent): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        return when (calendar[Calendar.DAY_OF_WEEK]) {
            Calendar.MONDAY -> intent.getBooleanExtra(MONDAY, false)
            Calendar.TUESDAY -> intent.getBooleanExtra(TUESDAY, false)
            Calendar.WEDNESDAY -> intent.getBooleanExtra(WEDNESDAY, false)
            Calendar.THURSDAY -> intent.getBooleanExtra(THURSDAY, false)
            Calendar.FRIDAY -> intent.getBooleanExtra(FRIDAY, false)
            Calendar.SATURDAY -> intent.getBooleanExtra(SATURDAY, false)
            Calendar.SUNDAY -> intent.getBooleanExtra(SUNDAY, false)
            else -> false
        }
    }

    private fun day(day: String) = when (day) {
        MONDAY -> "Monday"
        TUESDAY -> "Tuesday"
        WEDNESDAY -> "Wednesday"
        THURSDAY -> "Thursday"
        FRIDAY -> "Friday"
        SATURDAY -> "Saturday"
        SUNDAY -> "Sunday"
        else -> "Wrong Day"
    }
}