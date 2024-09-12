package com.hblab.alarmapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.hblab.alarmapp.data.model.AlarmItem

class AlarmDiffUtil : DiffUtil.ItemCallback<AlarmItem>() {

    override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem) =
        oldItem.alarmId == newItem.alarmId

    override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem) = oldItem == newItem

}