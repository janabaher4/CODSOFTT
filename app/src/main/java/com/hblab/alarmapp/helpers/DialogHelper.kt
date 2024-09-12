package com.hblab.alarmapp.helpers

import android.content.Context
import com.hblab.alarmapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogHelper {

    fun showDialog(context: Context, dialogInterface: DialogInterface) {
        MaterialAlertDialogBuilder(context, R.style.AlertDialog)
            .setTitle("Delete")
            .setMessage("Are you sure you want delete all alarms?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                dialogInterface.onRespond(true)
            }
            .setNegativeButton("No") { dialog, _ ->
                // else dismiss the dialog
                dialogInterface.onRespond(false)
                dialog.dismiss()
            }
            .create()
            .show()
    }

    interface DialogInterface {
        fun onRespond(respond: Boolean)
    }
}