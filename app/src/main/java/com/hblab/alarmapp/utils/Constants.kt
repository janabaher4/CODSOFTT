package com.hblab.alarmapp.utils

class Constants {

    companion object {

        // notification channel
        const val CHANNEL_ID = "alarm_channel"
        const val MAIN_NOTIFICATION_ID = 1

        // alarm item
        const val ALARM_ID = "alarm_id"
        const val ALARM_LABEL = "alarm_name"
        const val ALARM_TIME = "alarm_time"
        // alarm days
        const val REPEATING = "repeating"
        const val TODAY = "Today"
        const val TOMORROW = "Tomorrow"
        const val MONDAY = "Monday"
        const val TUESDAY = "Tuesday"
        const val WEDNESDAY = "Wednesday"
        const val THURSDAY = "Thursday"
        const val FRIDAY = "Friday"
        const val SATURDAY = "Saturday"
        const val SUNDAY = "Sunday"

        // ring time
        const val SNOOZE_TIME = "snooze_time"

        // intent actions
        const val ACTION_START_SERVICE = "com.example.alarmapp.ACTION_START_SERVICE"
        const val ACTION_DISMISS_ALARM = "com.example.alarmapp.ACTION_DISMISS_ALARM"
        const val ACTION_UPDATE_ALARM = "com.example.alarmapp.ACTION_UPDATE_ALARM"
        const val ACTION_ALARM_RECEIVER = "com.example.alarmapp.ACTION_ALARM_RECEIVER"
        const val ACTION_SNOOZE_ALARM = "com.example.alarmapp.ACTION_SNOOZE_ALARM"
        const val ACTION_SHOW_SNOOZE_TOAST = "com.example.alarmapp.ACTION_SHOW_SNOOZE_TOAST"
        const val ACTION_SHOW_DISMISS_TOAST = "com.example.alarmapp.ACTION_SHOW_DISMISS_TOAST"

        // pending intent
        const val REQUEST_CODE = "request_code"

        // current working thread
        const val CURRENT_THREAD_ID = "current_thread_id"
    }
}