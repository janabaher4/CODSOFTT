package com.hblab.alarmapp.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.hblab.alarmapp.R
import com.hblab.alarmapp.services.AlarmService
import com.hblab.alarmapp.utils.CalendarUtil
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_ALARM_RECEIVER
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_SHOW_DISMISS_TOAST
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_SHOW_SNOOZE_TOAST
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_ID
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.hblab.alarmapp.utils.Constants.Companion.REPEATING
import com.hblab.alarmapp.utils.Constants.Companion.SNOOZE_TIME
import com.hblab.alarmapp.utils.Messages

private const val TAG = "broadcastReceiver"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            // AlarmManager will send this action at scheduled time
            ACTION_ALARM_RECEIVER -> {
                val calendarUtil = CalendarUtil()

                // if it's repeating alarm, check if today scheduled then fire alarm
                // otherwise do nothing
                if (intent.getBooleanExtra(REPEATING, false)) {
                    if (calendarUtil.isTodayScheduled(intent))
                        ContextCompat.startForegroundService(context, serviceIntent(context, intent))
                } else
                // if it's a single day alarm fire alarm
                    ContextCompat.startForegroundService(context, serviceIntent(context, intent))

            }
            // snooze action button from heads-up notification
            // this action to only show toast message
            ACTION_SHOW_SNOOZE_TOAST -> {
                val snoozeTime = intent.getIntExtra(SNOOZE_TIME, 0)
                Messages.showToast(
                    context,
                    context.getString(R.string.snoozed_for, snoozeTime)
                )
            }
            // dismiss action button from heads-up notification
            // this action to only show toast message
            ACTION_SHOW_DISMISS_TOAST -> {
                Messages.showToast(context, context.getString(R.string.alarm_dismissed))
            }
        }
    }

    // service intent
    private fun serviceIntent(
        context: Context,
        intent: Intent
    ): Intent {
        return Intent(context, AlarmService::class.java).apply {
            action = ACTION_START_SERVICE
            putExtra(ALARM_LABEL, intent.getStringExtra(ALARM_LABEL))
            putExtra(ALARM_TIME, intent.getStringExtra(ALARM_TIME))
            putExtra(ALARM_ID, intent.getLongExtra(ALARM_ID, 0))
        }
    }

}