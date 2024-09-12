package com.hblab.alarmapp.ui.fragments

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hblab.alarmapp.R
import com.hblab.alarmapp.databinding.FragmentSnoozeDismissBinding
import com.hblab.alarmapp.utils.Constants.Companion.SNOOZE_TIME
import com.hblab.alarmapp.utils.isDarkTheme

class SnoozeDismissFragment : Fragment() {

    // view binding
    private lateinit var binding: FragmentSnoozeDismissBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSnoozeDismissBinding.inflate(inflater, container, false)

        // get arguments if not null
        val snoozeTimeString = arguments?.getString(SNOOZE_TIME)

        binding.tvSnoozeDismissFragment.text =
            snoozeTimeString ?: resources.getString(R.string.alarm_dismissed)

        changeColorAnimation(binding.root, binding.tvSnoozeDismissFragment)
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.finish()
        }, 4000)
        return binding.root
    }

    private fun changeColorAnimation(rootView: View, textView: TextView) {

        val startRootViewColor = rootView.solidColor
        val endRootViewColor =
            if (!requireContext().isDarkTheme()) 0xFFF88909.toInt() else 0x434343

        ObjectAnimator.ofInt(rootView, "backgroundColor", startRootViewColor, endRootViewColor)
            .apply {
                duration = 2000
                setEvaluator(ArgbEvaluator())
                repeatCount = 1
                repeatMode = ValueAnimator.REVERSE
                start()
            }
        val startTextViewColor = textView.solidColor
        val endTextViewColor = Color.WHITE
        ObjectAnimator.ofInt(textView, "textColor", startTextViewColor, endTextViewColor).apply {
            duration = 2000
            setEvaluator(ArgbEvaluator())
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }
}