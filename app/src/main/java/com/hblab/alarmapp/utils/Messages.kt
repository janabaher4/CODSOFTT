package com.hblab.alarmapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.model.AlarmItem
import com.google.android.material.snackbar.Snackbar

object Messages {

    fun showScheduledMessage(rootView: View, alarmItem: AlarmItem?, context: Context?) {
        // we already set the calendar and saved to alarmDate variable, but after
        // we check if the time is passed, alarmDate variable value will not change,
        // so, we need to set the calendar again
        alarmItem?.let {
            val calendarUtil = CalendarUtil()
            calendarUtil.setCalendar(it).apply {
                it.alarmDay = calendarUtil.getAlarmDay(context!!, this)
            }

            val alarmTimeString = calendarUtil.formatCalendarTime(it)
            createSnack(
                rootView,
                context?.getString(R.string.alarm_scheduled, it.alarmDay, alarmTimeString)
            )
        }
    }

    // create snack bar message
    fun createSnack(view: View, message: String?) =
        message?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show() }

    // create toast message
    fun showToast(context: Context, text: String?) =
        text?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
}