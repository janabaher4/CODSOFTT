package com.hblab.alarmapp.ui.fragments

import android.app.AlarmManager
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.model.AlarmItem
import com.hblab.alarmapp.data.repository.AlarmRepository
import com.hblab.alarmapp.data.room.AlarmDatabase
import com.hblab.alarmapp.databinding.FragmentAlarmsBinding
import com.hblab.alarmapp.helpers.AlarmHelper
import com.hblab.alarmapp.helpers.DialogHelper
import com.hblab.alarmapp.ui.adapters.AlarmsListAdapter
import com.hblab.alarmapp.ui.interfaces.AlarmViewsOnClickListener
import com.hblab.alarmapp.ui.viewmodels.AlarmViewModel
import com.hblab.alarmapp.ui.viewmodels.AlarmViewModelFactory
import com.hblab.alarmapp.utils.Messages
import com.hblab.alarmapp.utils.isDarkTheme
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView


class AlarmsFragment : Fragment(), AlarmViewsOnClickListener, Toolbar.OnMenuItemClickListener {

    private lateinit var binding: FragmentAlarmsBinding
    private lateinit var viewModel: AlarmViewModel
    private var alarmList = mutableListOf<AlarmItem>()
    private lateinit var alarmManager: AlarmManager
    private lateinit var adapter: AlarmsListAdapter
    private lateinit var rvMain: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentAlarmsBinding.inflate(inflater, container, false)

        // toolbar
        toolbar = binding.toolbar
        toolbar.setOnMenuItemClickListener(this)
        // drawer
//        drawer = binding.drawerLayout
//        navView = binding.navigationView
//        openCloseDrawer()

        // if dark theme is enabled change image
        if (requireContext().isDarkTheme()) {
            //val image = ContextCompat.getDrawable(requireContext(), )
            binding.ivAlarms.setImageResource(R.drawable.moon)
        }

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

        // initialize alarm manager
        alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // get reference to the adapter class
        adapter = AlarmsListAdapter(requireContext(), this)

        // init recycler view
        rvMain = binding.rvAlarmsFragment
        // set layout for recyclerview
        rvMain.layoutManager = LinearLayoutManager(context)

        // set the recyclerview on main_fragment adapter to the adapter class
        rvMain.adapter = adapter

        // get and observe all alarms from view model
        viewModel.alarms().observe(viewLifecycleOwner) { listOfAlarms ->

            // hide empty list message
            binding.tvNoAlarms.visibility = View.GONE
            // assign the list to the list in this fragment to use it
            // later to edit or delete items
            this.alarmList = listOfAlarms.toMutableList()
            // send the alarms list to the adapter by
            // using submitList method from ListAdapter class
            adapter.submitList(listOfAlarms)
            // if alarms list is empty show empty list message
            if (listOfAlarms.isEmpty())
                binding.tvNoAlarms.visibility = View.VISIBLE
        }

        // add new alarm fab
        binding.fabAddNewAlarm.setOnClickListener {
            findNavController().navigate(R.id.action_alarmsFragment_to_setAlarmFragment)
        }

        // returning the root element of the associated xml layout
        return binding.root
    }

    private fun openCloseDrawer() {

        if (requireContext().isDarkTheme()) {
            val colorInt = ContextCompat.getColor(requireContext(), R.color.white)
            val csl = ColorStateList.valueOf(colorInt)
            navView.itemTextColor = csl
            navView.itemIconTintList = null
        }

//        navView.setCheckedItem(R.id.drawer_alarms_home)

        toolbar.setNavigationOnClickListener {
            drawer.open()
        }

//        navView.setNavigationItemSelectedListener { menuItem ->
//            // Handle menu item selected
//            when (menuItem.itemId) {
//                R.id.drawer_settings -> findNavController().navigate(R.id.action_alarmsFragment_to_settingsFragment)
//            }
//            drawer.close()
//            true
//        }
    }

    override fun onSwitchToggle(position: Int) {
        val alarmItem = alarmList[position]

        val alarmHelper = AlarmHelper(requireContext())

        // on switch button state change
        if (alarmItem.isScheduled) {
            // call the cancelAlarm method to cancel the alarm and
            // change the isScheduled value to false ...
            val updatedAlarm = alarmHelper.cancelAlarm(alarmItem)
            // update alarm item on database
            viewModel.updateAlarm(updatedAlarm)
            rvMain.post { adapter.notifyItemChanged(position) }
            Messages.createSnack(binding.root, getString(R.string.alarm_cancelled))

        } else {
            // call the scheduleAlarm method to schedule the alarm and
            // change the isScheduled value to true ...
            val updatedAlarm = alarmHelper.scheduleAlarm(alarmItem)
            // update alarm item on database
            viewModel.updateAlarm(updatedAlarm)
            // to refresh this recyclerview item, we need to call notifyItemChanged(position)
            // on UI thread to avoid:
            // (IllegalStateException: Cannot call this method while
            // RecyclerView is computing a layout or scrolling).
            // here by using post method from View class, we are adding
            // the runnable block (adapter.notifyItemChanged(position)),
            // to the View (RecyclerView) message queue, and run it on user interface (UI)
            rvMain.post { adapter.notifyItemChanged(position) }
            Messages.showScheduledMessage(binding.root, alarmItem, context)

        }
    }

    // for recyclerview item buttons
    override fun onItemButtonClicked(btn: View, position: Int) {
        when (btn.id) {
            R.id.btn_edit_alarm -> {
                findNavController().navigate(
                    AlarmsFragmentDirections.actionAlarmsFragmentToSetAlarmFragment(
                        alarmList[position]
                    )
                )
            }
            R.id.btn_delete_alarm -> {
                val alarmItem = alarmList[position]
                val alarmHelper = AlarmHelper(requireContext())
                // cancel alarm if scheduled
                if (alarmItem.isScheduled) alarmHelper.cancelAlarm(alarmItem)
                // snack bar
                Messages.createSnack(binding.root, getString(R.string.alarm_deleted))
                // delete the alarm item from database
                viewModel.deleteAlarm(alarmItem)

            }
        }
    }

    // material toolbar menu items
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_all_alarm -> {
                // ask user for confirmation by using
                // AlertDialog to show popup message
                if (alarmList.isNotEmpty()) {
                    DialogHelper.showDialog(
                        requireContext(),
                        object : DialogHelper.DialogInterface {
                            override fun onRespond(respond: Boolean) {
                                if (respond) {
                                    // before deleting the alarms need to loop over all alarms
                                    // and check if any of them is already scheduled.
                                    // if yes then cancel it
                                    alarmList.forEach {
                                        if (it.isScheduled) {
                                            val alarmHelper = AlarmHelper(requireContext())
                                            alarmHelper.cancelAlarm(it)
                                        }
                                    }
                                    // snack bar
                                    Messages.createSnack(
                                        binding.root,
                                        getString(R.string.delete_all)
                                    )
                                    // if yes clicked delete alarm item
                                    viewModel.deleteAllAlarms()

                                }
                            }
                        })
                } else {
                    Messages.createSnack(binding.root, getString(R.string.delete_empty_list))
                }
            }
            R.id.alarm_settings -> {
                findNavController().navigate(R.id.action_alarmsFragment_to_settingsFragment)
            }
        }
        return true
    }
}