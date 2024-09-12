package com.hblab.alarmapp.ui.interfaces

import android.view.View

interface AlarmViewsOnClickListener {
    fun onSwitchToggle(position: Int)
    fun onItemButtonClicked(btn: View, position: Int)
}