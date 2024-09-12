package com.hblab.alarmapp.data.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hblab.alarmapp.R
import com.hblab.alarmapp.utils.snoozeTimeInt

class Storage(context: Context) {

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    fun getVibrate() = pref.getBoolean("key_vibrate", false)
    fun darkThemeOnOff() = pref.getBoolean("key_switch_theme", false)
    fun getSnoozeTime() = pref.getString("key_snooze_time", "5 Minutes")?.snoozeTimeInt()


    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )

    fun editPref(name: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(name, value)
            apply()
        }
    }
}