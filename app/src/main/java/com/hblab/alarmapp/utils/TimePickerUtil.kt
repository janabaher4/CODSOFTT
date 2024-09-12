package com.hblab.alarmapp.utils

import android.os.Build
import android.widget.TimePicker

class TimePickerUtil {

    fun getTime(timePicker: TimePicker): Pair<Int, Int> {

        val hour: Int
        val minute: Int

        // if android version == M (API Level 23) and later ...
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.hour
            minute = timePicker.minute
        }
        else {
            // we are targeting api level 21 so we need the following
            hour = timePicker.currentHour
            minute = timePicker.currentMinute
        }

        // return the two variables
        return Pair(hour, minute)
    }

    fun setTime(timePicker: TimePicker, hour: Int?, minute: Int?) {
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.apply {
                this.hour = hour ?: 12
                this.minute = minute ?: 0
            }
        } else {
            timePicker.apply {
                currentHour = hour ?: 12
                currentMinute = minute ?: 0
            }
        }
    }
}