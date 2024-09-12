package com.hblab.alarmapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hblab.alarmapp.data.model.AlarmItem

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmItem: AlarmItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(alarmItem: AlarmItem)

    @Query("UPDATE alarm_table SET isScheduled = :isScheduled WHERE alarmId = :id")
    suspend fun updateScheduled(isScheduled: Boolean, id: Long)

    @Delete
    suspend fun delete(alarmItem: AlarmItem)

    @Query("DELETE FROM `alarm_table`")
    suspend fun deleteAllAlarms()

    @Query("SELECT * FROM alarm_table WHERE alarmId LIKE :id")
    fun getAlarm(id: Long): LiveData<AlarmItem>

    @Query("SELECT * FROM alarm_table WHERE alarmId LIKE :id")
    fun getAlarmForBroadcast(id: Long): AlarmItem

    @Query("SELECT * FROM `alarm_table` ORDER BY currentTime DESC")
    fun getAllAlarms(): LiveData<List<AlarmItem>>

}