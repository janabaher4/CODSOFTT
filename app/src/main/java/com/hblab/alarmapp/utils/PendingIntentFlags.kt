package com.hblab.alarmapp.utils

import android.app.PendingIntent
import android.os.Build

fun updatePendingIntentFlag() : Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    else
       PendingIntent.FLAG_UPDATE_CURRENT
}