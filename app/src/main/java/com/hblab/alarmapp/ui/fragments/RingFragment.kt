package com.hblab.alarmapp.ui.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.room.AlarmDatabase
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.data.repository.AlarmRepository
import com.hblab.alarmapp.data.sharedpreferences.Storage
import com.hblab.alarmapp.databinding.FragmentRingBinding
import com.hblab.alarmapp.services.AlarmService
import com.hblab.alarmapp.ui.viewmodels.AlarmViewModel
import com.hblab.alarmapp.ui.viewmodels.AlarmViewModelFactory
import com.hblab.alarmapp.helpers.AlarmHelper
import com.hblab.alarmapp.utils.Constants
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_ID
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.hblab.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.hblab.alarmapp.utils.Constants.Companion.SNOOZE_TIME

private const val TAG = "ringFragment"

class RingFragment : Fragment(), View.OnClickListener {

    // view binding
    private lateinit var binding: FragmentRingBinding
    private lateinit var viewModel: AlarmViewModel
    private lateinit var alarmItem: AlarmItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRingBinding.inflate(inflater, container, false)

        // animate ring image
        ringImageAnimation(binding.ivRing)

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // get intent extras from foreground service
        val (alarmId, alarmLabel, alarmTime) = getIntentExtra()
        // get alarm item from database
        viewModel.getAlarm(alarmId!!).observe(viewLifecycleOwner) { alarmItem = it }
        // set text views values
        binding.tvRingAlarmLabel.text = alarmLabel
        binding.tvRingAlarmTime.text = alarmTime

        // buttons on click listener
        binding.btnRingSnooze.setOnClickListener(this)
        binding.btnRingDismiss.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        val alarmHelper = AlarmHelper(requireContext())

        when (v) {
            binding.btnRingSnooze -> {
                // stop foreground service
                activity?.stopService(stopServiceIntent())

                val storage = Storage(requireContext())
                val snoozeTime = storage.getSnoozeTime()

                val updatedAlarmItem = alarmHelper.snooze(snoozeTime!!, alarmItem)
                viewModel.updateAlarm(updatedAlarmItem)

                val args = Bundle().apply {
                    putString(
                        SNOOZE_TIME, context?.getString(R.string.snoozed_for, snoozeTime)
                    )
                }
                // navigate to SnoozeDismissFragment and send arguments to it
                val snoozeDismissFragment = SnoozeDismissFragment()
                snoozeDismissFragment.arguments = args
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.ring_activity_fragment_container, snoozeDismissFragment)
                    commit()
                }
            }
            // on dismiss button clicked ...
            binding.btnRingDismiss -> {
                // stop foreground service
                activity?.stopService(stopServiceIntent())

                val updatedAlarmItem = alarmHelper.dismiss(alarmItem)
                viewModel.updateAlarm(updatedAlarmItem)

                // navigate to SnoozeDismissFragment
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.ring_activity_fragment_container, SnoozeDismissFragment())
                    commit()
                }
            }
        }
    }

    // get intent extras from foreground service
    private fun getIntentExtra(): Triple<Long?, String?, String?> {
        val alarmId = activity?.intent?.getLongExtra(ALARM_ID, 0)
        val alarmLabel = activity?.intent?.getStringExtra(ALARM_LABEL)
        val alarmTime = activity?.intent?.getStringExtra(ALARM_TIME)
        return Triple(alarmId, alarmLabel, alarmTime)
    }

    private fun ringImageAnimation(ringImage: View) {
        ObjectAnimator.ofFloat(ringImage, "rotation", 0f, 20f, 0f, -20f, 0f).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 800
        }.start()
    }

    private fun stopServiceIntent() = Intent(activity, AlarmService::class.java).apply {
        action = Constants.ACTION_DISMISS_ALARM
    }
}