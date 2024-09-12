package com.hblab.alarmapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.ui.interfaces.AlarmViewsOnClickListener
import com.hblab.alarmapp.utils.CalendarUtil
import com.hblab.alarmapp.utils.applyBackground
import com.hblab.alarmapp.utils.isDarkTheme
import com.google.android.material.switchmaterial.SwitchMaterial

// View Holder class
class AlarmItemViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // init views
    private val infoLayout: ConstraintLayout = itemView.findViewById(R.id.info_layout)
    private val actionLayout: LinearLayout = itemView.findViewById(R.id.action_layout)
    private val tvLabel: TextView = itemView.findViewById(R.id.item_alarm_label)
    private val tvDay: TextView = itemView.findViewById(R.id.item_tv_day)
    private val tvTime: TextView = itemView.findViewById(R.id.item_tv_alarm_time)
    private val switchBtn: SwitchMaterial = itemView.findViewById(R.id.item_btn_switch)
    private val edit: ImageButton = itemView.findViewById(R.id.btn_edit_alarm)
    private val delete: ImageButton = itemView.findViewById(R.id.btn_delete_alarm)
    private val tvRemainTime: TextView = itemView.findViewById(R.id.item_tv_alarm_remain_time)
    private val repeatLayout: LinearLayout = itemView.findViewById(R.id.item_layout_repeat)
    private val monday: TextView = itemView.findViewById(R.id.item_tv_mo)
    private val tuesday: TextView = itemView.findViewById(R.id.item_tv_tu)
    private val wednesday: TextView = itemView.findViewById(R.id.item_tv_we)
    private val thursday: TextView = itemView.findViewById(R.id.item_tv_th)
    private val friday: TextView = itemView.findViewById(R.id.item_tv_fr)
    private val saturday: TextView = itemView.findViewById(R.id.item_tv_sa)
    private val sunday: TextView = itemView.findViewById(R.id.item_tv_su)


    // get views from data binding
    fun bind(alarmItem: AlarmItem, clickListener: AlarmViewsOnClickListener, context: Context) {
        val calendarUtil = CalendarUtil()
        // format alarm time
        val alarmTime = calendarUtil.formatCalendarTime(alarmItem)
        // set calendar time
        val alarmDate = calendarUtil.setCalendar(alarmItem)

        val alarmDay = calendarUtil.getAlarmDay(context ,alarmDate)

        // alarm manager
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        // remain time before alarm goes off
        val remainTime = alarmDate.timeInMillis - System.currentTimeMillis()
        //val seconds = (remainTime / 1000) % 60
        val hours = (remainTime.div(1000.times(60).times(60))).rem(24)
        val minutes = (remainTime.div(1000.times(60))).rem(60)

        // set time text view value
        tvTime.text = alarmTime
        // if alarm scheduled
        if (alarmItem.isScheduled) {
            if (alarmItem.isRepeating) {
                // change time textView text color
                tvTime.setTextColor(
                    if (context.isDarkTheme())
                        ContextCompat.getColor(context, R.color.dark_orange)
                    else ContextCompat.getColor(context, R.color.orange)
                )
                // set alarm label text view value
                tvLabel.text = (alarmItem.alarmLabel)
                // change scheduled days background color
                changeDayBackground(alarmItem)
                // show repeating days layout
                repeatLayout.visibility = View.VISIBLE

            } else {
                repeatLayout.visibility = View.GONE
                // change time textView text color
                tvTime.setTextColor(
                    if (context.isDarkTheme())
                        ContextCompat.getColor(context, R.color.dark_orange)
                    else ContextCompat.getColor(context, R.color.orange)
                )
                // expand info layout
                infoLayout.visibility = View.VISIBLE
                // set alarm label text view value
                tvLabel.text = (alarmItem.alarmLabel)
                // set alarm day text view value
                tvDay.text = alarmDay
                // set remain time text view value
                //tvRemainTime.text = remainTime.toString()
                tvRemainTime.text = when {
                    hours > 0 && hours != 1L -> context.getString(R.string.remain_time_full, hours, minutes)
                    hours == 1L -> context.getString(R.string.remain_time_hour_full, hours, minutes)
                    minutes > 1 -> context.getString(R.string.remain_time_minutes, minutes)
                    minutes == 1L -> context.getString(R.string.remain_time_minute)
                    else -> context.getString(R.string.remain_time_less_than_minute)
                }
            }

            actionLayout.visibility = View.VISIBLE

        } else {
            // if alarm off or not scheduled,
            // hide info layout and repeating days layout, and restore
            // alarm time textView default color
            infoLayout.visibility = View.GONE
            repeatLayout.visibility = View.GONE
            tvTime.setTextColor(ContextCompat.getColor(context, R.color.text_color))

            actionLayout.visibility = View.GONE
        }

        // switch button will follow isScheduled state of the alarm item
        // if isScheduled == true then switch isChecked == true
        switchBtn.isChecked = alarmItem.isScheduled
        // switch on click listener
        switchBtn.setOnClickListener {
            clickListener.onSwitchToggle(adapterPosition)
        }

        // delete btn on click listener
        edit.setOnClickListener { clickListener.onItemButtonClicked(it, adapterPosition) }

        // delete btn on click listener
        delete.setOnClickListener { clickListener.onItemButtonClicked(it, adapterPosition) }

    }

    private fun changeDayBackground(alarmItem: AlarmItem) {
//        val bgColor = if (Extras.isDarkTheme(context))
//            ContextCompat.getColor(context, R.color.dark_orange)
//        else ContextCompat.getColor(context, R.color.orange)

        if (alarmItem.isRepeating) {
                if(alarmItem.isMonday) monday.applyBackground()
                if(alarmItem.isTuesday) tuesday.applyBackground()
                if(alarmItem.isWednesday) wednesday.applyBackground()
                if(alarmItem.isThursday) thursday.applyBackground()
                if(alarmItem.isFriday) friday.applyBackground()
                if(alarmItem.isSaturday) saturday.applyBackground()
                if(alarmItem.isSunday) sunday.applyBackground()
        }
    }

    companion object {
        fun from(parent: ViewGroup): AlarmItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.alarm_item,
                parent,
                false
            )
            //Log.d(TAG, "on create view holder")
            return AlarmItemViewHolder(view)
        }

    }

}