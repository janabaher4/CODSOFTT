package com.hblab.alarmapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.sharedpreferences.Storage
import com.hblab.alarmapp.databinding.ActivityMainBinding
import com.hblab.alarmapp.utils.isDarkTheme

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        //setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
    }

    override fun onStart() {
        super.onStart()

        val storage = Storage(this)
        val appDefaultMode = storage.darkThemeOnOff()
        val deviceDefaultMode = this.isDarkTheme()

        if (deviceDefaultMode or appDefaultMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            storage.editPref(getString(R.string.key_switch_theme), true)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            storage.editPref(getString(R.string.key_switch_theme), false)
        }
    }


    // to activate back navigation from the fragment action bar arrow
    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
}
