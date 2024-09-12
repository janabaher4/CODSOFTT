package com.hblab.alarmapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.hblab.alarmapp.R
import com.hblab.alarmapp.data.sharedpreferences.Storage
import com.google.android.material.appbar.MaterialToolbar

const val KEY_RINGTONE_PREFERENCE = "key_alarm_ringtone"

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var darkModeSwitch: SwitchPreference
    private lateinit var storage: Storage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.preference_fragment_toolbar).apply {
            title = resources.getString(R.string.settings)
        }

        storage = Storage(requireContext())

        darkModeSwitch = findPreference(resources.getString(R.string.key_switch_theme))!!

        darkModeSwitch.setOnPreferenceClickListener {
                // implement dark mode
                if (darkModeSwitch.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    storage.editPref(getString(R.string.key_switch_theme), true)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    storage.editPref(getString(R.string.key_switch_theme), false)
                }

            return@setOnPreferenceClickListener false
        }

        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_alarmsFragment)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

    }



//        val ringtoneTitle =
//            findPreference<Preference>(resources.getString(R.string.key_alarm_ringtone))

//    private var launchRingManager: ActivityResultLauncher<Intent>? = null
//
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource(R.xml.preferences, rootKey)
//
//        val ringtoneTitle =
//            findPreference<Preference>(resources.getString(R.string.key_alarm_ringtone))
//
//        launchRingManager =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    val data: Intent? = result.data
//
//                    val ringtone =
//                        data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
//                    ringtoneTitle?.summary = ringtone?.getQueryParameter("title")
//                        ?: resources.getString(R.string.summary_choose_ringtone)
//
//                    //save ringtone uri
//                    PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
//                        putString(
//                            resources.getString(R.string.key_alarm_ringtone),
//                            ringtone.toString()
//                        )
//                    }.apply()
//                }
//            }
//    }
//
//
//    override fun onPreferenceTreeClick(preference: Preference): Boolean {
//        return if (preference.key == KEY_RINGTONE_PREFERENCE) {
//            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
//            intent.putExtra(
//                RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
//                Settings.System.DEFAULT_NOTIFICATION_URI
//            )
//
//            launchRingManager?.launch(intent)
//
//            true
//        } else {
//            super.onPreferenceTreeClick(preference)
//        }
//    }
}
