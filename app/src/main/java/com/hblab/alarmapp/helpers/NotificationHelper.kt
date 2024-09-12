package com.hblab.alarmapp.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.sharedpreferences.Storage
import com.hblab.alarmapp.services.AlarmService
import com.hblab.alarmapp.ui.activities.RingActivity
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_DISMISS_ALARM
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_SNOOZE_ALARM
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_ID
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.hblab.alarmapp.utils.Constants.Companion.CHANNEL_ID
import com.hblab.alarmapp.utils.updatePendingIntentFlag


class NotificationHelper(private val context: Context) {

    private val storage = Storage(context)
    private val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) as Uri
    private val vibrate = storage.getVibrate()

    fun createNotification(
        alarmId: Long?,
        alarmLabel: String?,
        alarmTime: String?
    ): Notification {

        val pattern = longArrayOf(0, 100, 1000)

        if (Build.VERSION.SDK_INT >= 26) { // android oreo api level 26
            // create notification channel
            createNotificationChannel()
            // create notification
            return with(NotificationCompat.Builder(context, CHANNEL_ID)) {
                setSmallIcon(R.drawable.ic_notifications)
                setContentTitle(alarmLabel)
                setContentText(alarmTime)
                priority = NotificationCompat.PRIORITY_HIGH
                setSound(ringtoneUri)
                if (vibrate) setVibrate(pattern)
                // delete intent
//                setDeleteIntent(
//                    notificationPendingIntents(
//                        alarmId,
//                        alarmLabel,
//                        alarmTime
//                    ).first
//                )
                // snooze action button
                addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.snooze),
                    notificationPendingIntents(alarmId, alarmLabel, alarmTime).first //
                )
                // dismiss action button
                addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.dismiss),
                    notificationPendingIntents(
                        alarmId,
                        alarmLabel,
                        alarmTime
                    ).second
                )
                // full screen intent (open ring activity)
                setFullScreenIntent(
                    notificationPendingIntents(
                        alarmId,
                        alarmLabel,
                        alarmTime
                    ).third, true
                )
                setAutoCancel(true)
            }.build()
        } else {
            // for android version older than (oreo api level 26)
            return with(NotificationCompat.Builder(context, CHANNEL_ID)) {
                setSmallIcon(R.drawable.ic_notifications)
                setContentTitle(alarmLabel)
                setContentText(alarmTime)
                priority = NotificationCompat.PRIORITY_HIGH
                setSound(ringtoneUri)
                if (vibrate) setVibrate(pattern)
                addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.dismiss),
                    notificationPendingIntents(
                        alarmId,
                        alarmLabel,
                        alarmTime
                    ).second // update alarm intent
                )
                addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.dismiss),
                    notificationPendingIntents(
                        alarmId,
                        alarmLabel,
                        alarmTime
                    ).second // update alarm intent
                )
                setContentIntent(
                    notificationPendingIntents(
                        alarmId,
                        alarmLabel,
                        alarmTime
                    ).third
                ) // // content intent
                setAutoCancel(true)
            }.build()
        }
    }

    // create pending intents to ship with notification
    private fun notificationPendingIntents(
        alarmId: Long?,
        alarmLabel: String?,
        alarmTime: String?
    ): Triple<PendingIntent, PendingIntent, PendingIntent> {

        // snooze intent
        val snoozeIntent = Intent(context, AlarmService::class.java).apply {
            action = ACTION_SNOOZE_ALARM
            putExtra(ALARM_ID, alarmId)
        }
        // snooze pending intent
        val snoozePendingIntent = PendingIntent.getService(
            context,
            0,
            snoozeIntent,
            updatePendingIntentFlag()
        )

        // dismiss intent
        val dismissIntent = Intent(context, AlarmService::class.java).apply {
            action = ACTION_DISMISS_ALARM
            putExtra(ALARM_ID, alarmId)
        }
        // dismiss pending intent
        val dismissPendingIntent = PendingIntent.getService(
            context,
            0,
            dismissIntent,
            updatePendingIntentFlag()// override snooze pending intent
        )

        // open ring activity intent
        val ringActivityIntent = Intent(context, RingActivity::class.java).apply {
            putExtra(ALARM_LABEL, alarmLabel)
            putExtra(ALARM_TIME, alarmTime)
            putExtra(ALARM_ID, alarmId)
        }
        // open ring activity pending intent
        val ringActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            ringActivityIntent,
            updatePendingIntentFlag() // override dismiss pending intent
        )

        // return the three pending intents
        return Triple(
            snoozePendingIntent,
            dismissPendingIntent,
            ringActivityPendingIntent
        )
    }

    // create notification channel for Android version starting from Oreo (API Level 26)
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {

        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Create the NotificationChannel
        val channelName = context.getString(R.string.alarm_app_channel_name)
        val channelDescription = context.getString(R.string.alarm_app_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, channelName, importance)
        // set channel values
        with(notificationChannel) {
            description = channelDescription
            enableVibration(true)
            setSound(ringtoneUri, att)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            setShowBadge(true)
            setBypassDnd(true)
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }


//    private fun setVibration() : Vibrator {
//        // Get instance of Vibrator from current Context
//        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//
//        // Start without a delay
//        // Vibrate for 100 milliseconds
//        // Sleep for 1000 milliseconds
//        val pattern = longArrayOf(0, 100, 1000)
//
//        vibrator.let {
//            if (Build.VERSION.SDK_INT >= 26) {
//                it.vibrate(
//                    VibrationEffect.createWaveform(
//                        pattern,
//                        VibrationEffect.DEFAULT_AMPLITUDE
//                    )
//                )
//            } else {
//                // The '0' here means to repeat indefinitely
//                // '0' is actually the index at which the pattern keeps repeating from (the start)
//                // To repeat the pattern from any other point, you could increase the index, e.g. '1'
//                @Suppress("DEPRECATION")
//                it.vibrate(pattern, 0)
//            }
//        }
//        return vibrator
//    }
}