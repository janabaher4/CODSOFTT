package com.hblab.alarmapp.ui.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.ui.interfaces.AlarmViewsOnClickListener
import com.hblab.alarmapp.utils.AlarmDiffUtil


class AlarmsListAdapter(
    private val context: Context,
    private val clickListener: AlarmViewsOnClickListener
) : ListAdapter<AlarmItem, AlarmItemViewHolder>(AlarmDiffUtil()) {

    // on create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmItemViewHolder =
        AlarmItemViewHolder.from(parent)

    // on bind view holder
    override fun onBindViewHolder(holder: AlarmItemViewHolder, position: Int) =
        holder.bind(getItem(position), clickListener, context)

}


