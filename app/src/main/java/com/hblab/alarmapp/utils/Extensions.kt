package com.hblab.alarmapp.utils

import android.content.Context
import android.content.res.Configuration
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.hblab.alarmapp.R

// check if dark theme enabled
fun Context.isDarkTheme(): Boolean =
    when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }

// get numbers from string
fun String.snoozeTimeInt() = this.filter { it.isDigit() }.toInt()


fun TextView.applyBackground() {
    this.background = AppCompatResources.getDrawable(context, R.drawable.day_bg)
}


