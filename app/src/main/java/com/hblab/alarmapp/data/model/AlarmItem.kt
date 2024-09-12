package com.hblab.alarmapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(
    tableName = "alarm_table",
    indices = [Index(value = ["hour", "minute", "alarmDay"], unique = true)]
)
data class AlarmItem(

    @PrimaryKey
    val alarmId: Long,
    var alarmLabel: String,
    var hour: Int,
    var minute: Int,
    var alarmDay: String,
    var isScheduled: Boolean = true,
    var isRepeating: Boolean = false,
    var isMonday: Boolean = false,
    var isTuesday: Boolean = false,
    var isWednesday: Boolean = false,
    var isThursday: Boolean = false,
    var isFriday: Boolean = false,
    var isSaturday: Boolean = false,
    var isSunday: Boolean = false,
    val currentTime: Long
) : Parcelable