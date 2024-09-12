package com.hblab.alarmapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.data.repository.AlarmRepository
import com.hblab.alarmapp.data.room.AlarmDatabase
import com.hblab.alarmapp.databinding.FragmentSetAlarmBinding
import com.hblab.alarmapp.ui.viewmodels.AlarmViewModelFactory
import com.hblab.alarmapp.ui.viewmodels.AlarmViewModel
import com.hblab.alarmapp.helpers.AlarmHelper
import com.hblab.alarmapp.utils.CalendarUtil
import com.hblab.alarmapp.utils.Constants.Companion.FRIDAY
import com.hblab.alarmapp.utils.Constants.Companion.MONDAY
import com.hblab.alarmapp.utils.Constants.Companion.SATURDAY
import com.hblab.alarmapp.utils.Constants.Companion.SUNDAY
import com.hblab.alarmapp.utils.Constants.Companion.THURSDAY
import com.hblab.alarmapp.utils.Constants.Companion.TUESDAY
import com.hblab.alarmapp.utils.Constants.Companion.WEDNESDAY
import com.hblab.alarmapp.utils.Messages
import com.hblab.alarmapp.utils.TimePickerUtil
import java.util.*
import kotlin.random.Random

private const val TAG = "setAlarmFragment"

class SetAlarmFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSetAlarmBinding
    private lateinit var viewModel: AlarmViewModel
    private val args: SetAlarmFragmentArgs by navArgs()
    private var isRepeating = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentSetAlarmBinding.inflate(inflater, container, false)

        // if is edit case get arguments
        // and set the values to views
        editAlarm()

        // get a reference to the application context. we need application context to
        // create database instance
        val application = requireNotNull(this.activity).application
        // create database and get reference to Dao Object
        val dataDao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
        // get reference to the repository class
        val repository = AlarmRepository(dataDao)
        //get instance of the viewModelFactory
        val viewModelFactory = AlarmViewModelFactory(repository)
        // initialize the ViewModel class
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmViewModel::class.java)

        // toolbar
        binding.setAlarmFragmentToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_setAlarmFragment_to_alarmsFragment)
        }

        // if the buttons are clicked use the implemented (View.OnClickListener) interface
        binding.btnSetAlarm.setOnClickListener(this)
        binding.btnCancelAndClose.setOnClickListener(this)
        binding.cbRepeat.setOnClickListener(this)

        // return the root element view of the associated xml layout
        return binding.root
    }

    private fun editAlarm() {
        if (args.alarmItem != null) {
            binding.etAlarmLabel.setText(args.alarmItem?.alarmLabel)
            TimePickerUtil().setTime(binding.timePicker, args.alarmItem?.hour, args.alarmItem?.minute)
            if (args.alarmItem!!.isRepeating) {
                // repeat check box
                binding.cbRepeat.isChecked = args.alarmItem!!.isRepeating
                isRepeating = daysChecked()
                // days check boxes
                binding.cbMo.isChecked = args.alarmItem!!.isMonday
                binding.cbTu.isChecked = args.alarmItem!!.isTuesday
                binding.cbWe.isChecked = args.alarmItem!!.isWednesday
                binding.cbTh.isChecked = args.alarmItem!!.isThursday
                binding.cbFr.isChecked = args.alarmItem!!.isFriday
                binding.cbSa.isChecked = args.alarmItem!!.isSaturday
                binding.cbSu.isChecked = args.alarmItem!!.isSunday
                binding.repeatDaysLinearLayout.visibility = View.VISIBLE
            } else {
                binding.repeatDaysLinearLayout.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        // repeat check boxes
        val repeatCheckBox = binding.cbRepeat
        val daysLayout = binding.repeatDaysLinearLayout
        // buttons
        val btnSetAlarm = binding.btnSetAlarm
        val btnClose = binding.btnCancelAndClose

        when (v) {
            // if repeat check box checked
            repeatCheckBox -> {
                if (repeatCheckBox.isChecked) {
                    isRepeating = true
                    // show days check boxes layout
                    daysLayout.visibility = View.VISIBLE
                } else {
                    isRepeating = false
                    clearDaysBoxes()
                    // hide days check boxes layout
                    daysLayout.visibility = View.GONE
                }
            }
            // if btn set alarm clicked
            btnSetAlarm -> setInsertAlarm(args.alarmItem)
            // if btn cancel clicked
            btnClose -> cancelAndClose()
        }
    }

    private fun setInsertAlarm(alarmItem: AlarmItem?) {
        val calendarUtil = CalendarUtil()
        val alarmHelper = AlarmHelper(requireContext())
        // alarm title
        var label = binding.etAlarmLabel.text.toString()
        if (label.isBlank()) label = getString(R.string.no_label)
        // get time picker time
        val (hour, minute) = TimePickerUtil().getTime(binding.timePicker)
        // set calendar time
        val alarmDate = calendarUtil.setCalendar(hour, minute)
        // if alarm time is passed add one day
        if (alarmDate.before(Calendar.getInstance())) alarmDate.add(Calendar.DATE, 1)
        // get the alarm day
        val alarmDay = calendarUtil.getAlarmDay(requireContext(), alarmDate)
        // set random number for the alarm id field in database

        // insert new alarm if alarm item null ...
        if (alarmItem == null)
            insertScheduleAlarm(label, hour, minute, alarmDay, alarmHelper)
        // or update existing alarm item
        else updateScheduleAlarm(alarmItem, label, hour, minute, alarmDay, alarmHelper)

        // navigate back to the main fragment
        findNavController().navigate(R.id.action_setAlarmFragment_to_alarmsFragment)
    }

    private fun insertScheduleAlarm(
        label: String,
        hour: Int,
        minute: Int,
        alarmDay: String,
        alarmHelper: AlarmHelper
    ) {
        // random id for alarm item
        val alarmId = Random.nextLong(1L, Long.MAX_VALUE)
        val repeatDays = getDaysCheckBoxesStates()

        // if no day checked set isRepeating to false
        isRepeating = daysChecked()

        // create alarm item
        val alarmItem = AlarmItem(
            alarmId,
            label,
            hour,
            minute,
            alarmDay,
            true,
            isRepeating,
            repeatDays[MONDAY]!!,
            repeatDays[TUESDAY]!!,
            repeatDays[WEDNESDAY]!!,
            repeatDays[THURSDAY]!!,
            repeatDays[FRIDAY]!!,
            repeatDays[SATURDAY]!!,
            repeatDays[SUNDAY]!!,
            System.currentTimeMillis()
        )

        // schedule alarm
        alarmHelper.scheduleAlarm(alarmItem)

        // insert alarm item into database
        viewModel.insertAlarm(alarmItem)

        Messages.showScheduledMessage(binding.root, alarmItem, context)
    }

    private fun updateScheduleAlarm(
        alarmItem: AlarmItem, label: String,
        hour: Int,
        minute: Int,
        alarmDay: String,
        alarmHelper: AlarmHelper
    ) {
        // cancel old alarm
        alarmHelper.cancelAlarm(alarmItem)

        val repeatDays = getDaysCheckBoxesStates()
        // update old alarm item with new values
        alarmItem.alarmLabel = label
        alarmItem.hour = hour
        alarmItem.minute = minute
        alarmItem.alarmDay = alarmDay

        // if no day checked set isRepeating to false
        alarmItem.isRepeating = daysChecked()

        if (alarmItem.isRepeating) {
            alarmItem.isMonday = repeatDays[MONDAY]!!
            alarmItem.isTuesday = repeatDays[TUESDAY]!!
            alarmItem.isWednesday = repeatDays[WEDNESDAY]!!
            alarmItem.isThursday = repeatDays[THURSDAY]!!
            alarmItem.isFriday = repeatDays[FRIDAY]!!
            alarmItem.isSaturday = repeatDays[SATURDAY]!!
            alarmItem.isSunday = repeatDays[SUNDAY]!!
        } else {
            alarmItem.isMonday = false
            alarmItem.isTuesday = false
            alarmItem.isWednesday = false
            alarmItem.isThursday = false
            alarmItem.isFriday = false
            alarmItem.isSaturday = false
            alarmItem.isSunday = false
        }

        // schedule alarm
        alarmHelper.scheduleAlarm(alarmItem)
        // update alarm item in database
        viewModel.updateAlarm(alarmItem)
        // show message
        Messages.showScheduledMessage(binding.root, alarmItem, context)
    }

    private fun getDaysCheckBoxesStates(): MutableMap<String, Boolean> {
        val boxesList = mutableMapOf<String, Boolean>()

        boxesList[MONDAY] = binding.cbMo.isChecked
        boxesList[TUESDAY] = binding.cbTu.isChecked
        boxesList[WEDNESDAY] = binding.cbWe.isChecked
        boxesList[THURSDAY] = binding.cbTh.isChecked
        boxesList[FRIDAY] = binding.cbFr.isChecked
        boxesList[SATURDAY] = binding.cbSa.isChecked
        boxesList[SUNDAY] = binding.cbSu.isChecked

        return boxesList
    }

    private fun clearDaysBoxes() {
        binding.cbMo.isChecked = false
        binding.cbTu.isChecked = false
        binding.cbWe.isChecked = false
        binding.cbTh.isChecked = false
        binding.cbFr.isChecked = false
        binding.cbSa.isChecked = false
        binding.cbSu.isChecked = false
    }

    private fun daysChecked(): Boolean {
        for ((_, value) in getDaysCheckBoxesStates())
            if (value) return true
        return false
    }

    // if cancel button clicked, navigate back to the main fragment and
    // we need to set pop to and inclusive in the navigation graph
    private fun cancelAndClose() =
        findNavController().navigate(R.id.action_setAlarmFragment_to_alarmsFragment)
}