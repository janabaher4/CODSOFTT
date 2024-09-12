package com.hblab.alarmapp.data.repository

import com.hblab.alarmapp.data.room.AlarmDao
import com.hblab.alarmapp.data.model.AlarmItem

class AlarmRepository(private var alarmDao: AlarmDao) {

    fun alarmsList() = alarmDao.getAllAlarms()

    fun getAlarm(id: Long) = alarmDao.getAlarm(id)

    fun getAlarmForBroadcast(id: Long) = alarmDao.getAlarmForBroadcast(id)

    suspend fun insert(alarmItem: AlarmItem) = alarmDao.insert(alarmItem)

    suspend fun update(alarmItem: AlarmItem) = alarmDao.update(alarmItem)

    suspend fun updateScheduled(isScheduled: Boolean, id: Long) = alarmDao.updateScheduled(isScheduled, id)

    suspend fun delete(alarmItem: AlarmItem) = alarmDao.delete(alarmItem)

    suspend fun deleteAllAlarms() = alarmDao.deleteAllAlarms()


}