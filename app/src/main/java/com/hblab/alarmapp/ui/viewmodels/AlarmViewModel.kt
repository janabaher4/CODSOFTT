package com.hblab.alarmapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.data.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {

    fun alarms() = repository.alarmsList()

    fun getAlarm(id: Long) =
        repository.getAlarm(id)

    fun insertAlarm(alarmItem: AlarmItem) {
        // do the work in background thread with coroutines
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(alarmItem)
        }
    }

    fun updateAlarm(alarmItem: AlarmItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(alarmItem)
        }
    }

    fun updateScheduled(isScheduled: Boolean, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateScheduled(isScheduled, id)
        }
    }

    fun deleteAlarm(alarmItem: AlarmItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(alarmItem)
        }
    }

    fun deleteAllAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllAlarms()
        }
    }

}