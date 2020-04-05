package com.nathanlee.habittracker.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import com.nathanlee.habittracker.R

class HabitDialog: AppCompatDialogFragment() {
    lateinit var colourRadioGroup: RadioGroup
    lateinit var habitNameTextView: TextView
    lateinit var titleTextView: TextView
    lateinit var habitNameEditText: EditText
    lateinit var habitDescriptionEditTextView: EditText
    lateinit var cancelTextView: TextView
    lateinit var saveTextView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.habit_popup, null)

        builder.setView(view)
            .setTitle("Create com.nathanlee.habittracker.models.Habit")

        habitNameTextView = view.findViewById<TextView>(R.id.habitNameText)

        colourRadioGroup = view.findViewById<RadioGroup>(R.id.colourGroup)
        colourRadioGroup.setOnCheckedChangeListener{ _, colourId ->

            habitNameTextView.setTextColor(selectColour(colourId))

        }

        titleTextView = view.findViewById<TextView>(R.id.popupTitleText)
        titleTextView.setText(R.string.habit_popup_title_new)


        habitNameEditText = view.findViewById<EditText>(R.id.habitNameText)
        habitNameEditText.setTextColor(selectColour(colourRadioGroup.checkedRadioButtonId))

        habitDescriptionEditTextView = view.findViewById<EditText>(R.id.habitDescriptionText)

        cancelTextView = view.findViewById<TextView>(R.id.cancelText)
        cancelTextView.setOnClickListener {
            closeNewHabitPopup(cancelTextView)
        }

        saveTextView = view.findViewById<TextView>(R.id.saveText)
        saveTextView.setOnClickListener {
            closeNewHabitPopup(saveTextView)
        }

        return builder.create()
    }

    fun closeNewHabitPopup(v: View) {
        if (v.id == R.id.saveText) {
            var selectedColour = colourRadioGroup.checkedRadioButtonId
            var name = habitNameEditText.text.toString()
            var description = habitDescriptionEditTextView.text.toString()

            // TODO: StartIntent to send info
        }

        this.dismiss()

    }

    fun selectColour(colourId: Int): Int{
        val darkTheme: IntArray = intArrayOf(
            R.color.darkThemeGrey,
            R.color.darkThemeBlue,
            R.color.darkThemeGreen,
            R.color.darkThemeOrange,
            R.color.darkThemePink,
            R.color.darkThemePurple,
            R.color.darkThemeYellow
        )

        when (colourId) {
            R.id.greyRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[0])
            }
            R.id.blueRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[1])
            }

            R.id.greenRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[2])
            }

            R.id.orangeRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[3])
            }

            R.id.pinkRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[4])
            }

            R.id.purpleRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[5])
            }

            R.id.yellowRadio -> {
                return ContextCompat.getColor(requireContext(), darkTheme[6])
            }
        }

        return ContextCompat.getColor(requireContext(), darkTheme[0])
    }
}