package com.hblab.alarmapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hblab.alarmapp.broadcastreceivers.AlarmReceiver
import com.hblab.alarmapp.data.room.AlarmDatabase
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.data.repository.AlarmRepository
import com.hblab.alarmapp.data.sharedpreferences.Storage
import com.hblab.alarmapp.helpers.AlarmHelper
import com.hblab.alarmapp.utils.CalendarUtil
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_SHOW_DISMISS_TOAST
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_SHOW_SNOOZE_TOAST
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_SNOOZE_ALARM
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.hblab.alarmapp.utils.Constants.Companion.ACTION_DISMISS_ALARM
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_ID
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.hblab.alarmapp.utils.Constants.Companion.SNOOZE_TIME
import com.hblab.alarmapp.helpers.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class AlarmService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // running the service on separate thread
        Thread {
            //Log.d(CURRENT_THREAD_ID, "onStartCommand Thread: ${Thread.currentThread().id}")

            // check intent action
            when (intent?.action) {

                ACTION_START_SERVICE -> {
                    // get intent extra from broadcast receiver
                    val alarmId = intent.getLongExtra(ALARM_ID, 0)
                    val alarmLabel = intent.getStringExtra(ALARM_LABEL)
                    val alarmTime = intent.getStringExtra(ALARM_TIME)

                    // create notification
                    val notification =
                        NotificationHelper(this).createNotification(alarmId, alarmLabel, alarmTime)
//                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                    manager.notify(alarmId.toInt(), notification)


                    // AutoCancel does not work when service is still on foreground.
                    // Try remove service from foreground:
                    // startForeground(2, notification);
                    // stopForeground(false); //false - do not remove generated notification
                    // https://stackoverflow.com/a/51390532/10609832
                    startForeground(alarmId.toInt(), notification)
                    //stopForeground(false)
                }
                ACTION_SNOOZE_ALARM -> {
                    scope.launch {
                        // get snooze time from sharedPreferences
                        val storage = Storage(application)
                        val snoozeTime = storage.getSnoozeTime()

                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = System.currentTimeMillis()
                        calendar.add(Calendar.MINUTE, snoozeTime!!)
                        // create database and get reference to Dao Object
                        val dataDao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
                        // get reference to the repository class
                        val repository = AlarmRepository(dataDao)


                        val alarmItem =
                            repository.getAlarmForBroadcast(intent.getLongExtra(ALARM_ID, 0))

                        // update alarm item time
                        alarmItem.hour = calendar.get(Calendar.HOUR_OF_DAY)
                        alarmItem.minute = calendar.get(Calendar.MINUTE)

                        val alarmHelper = AlarmHelper(application)
                        alarmHelper.scheduleAlarm(alarmItem)
                        repository.update(alarmItem)

                        sendToBroadcast(ACTION_SHOW_SNOOZE_TOAST, snoozeTime, alarmItem)

                        // stop the service when done
                        stopSelf()
                    }
                }
                ACTION_DISMISS_ALARM -> {

                    // get snooze time from sharedPreferences
                    val storage = Storage(application)
                    val snoozeTime = storage.getSnoozeTime()

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.add(Calendar.MINUTE, snoozeTime!!)
                    // create database and get reference to Dao Object
                    val dataDao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
                    // get reference to the repository class
                    val repository = AlarmRepository(dataDao)
                    val alarmItem =
                        repository.getAlarmForBroadcast(intent.getLongExtra(ALARM_ID, 0))
                    // update alarm item in database set isScheduled = false
                    //val alarmId = intent.getLongExtra(ALARM_ID, 0)

                    updateIsScheduled(alarmItem)

                    // show toast message
                    sendToBroadcast(broadcastAction = ACTION_SHOW_DISMISS_TOAST)

                    // stop the service
                    stopSelf()
                }
            }
        }.start()

        return START_NOT_STICKY
    }

    private fun sendToBroadcast(
        broadcastAction: String,
        snoozeTime: Int = 10,
        alarmItem: AlarmItem? = null
    ) {
        var alarmTimeString = ""
        if (alarmItem != null) {
            alarmTimeString = CalendarUtil().formatCalendarTime(alarmItem)
        }

        val toastIntent = Intent(application, AlarmReceiver::class.java).apply {
            action = broadcastAction
            putExtra(ALARM_TIME, alarmTimeString)
            putExtra(SNOOZE_TIME, snoozeTime)
        }

        sendBroadcast(toastIntent)
    }

    private fun updateIsScheduled(alarmItem: AlarmItem) {

        val dao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
        val repository = AlarmRepository(dao)

        CoroutineScope(Dispatchers.IO).launch {
            if (!alarmItem.isRepeating)
                repository.updateScheduled(false, alarmItem.alarmId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}