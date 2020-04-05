package com.nathanlee.habittracker.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nathanlee.habittracker.R

class MainActivity : AppCompatActivity() {
    lateinit var habitDialog: Dialog
    lateinit var settingsButton: ImageButton
    lateinit var colourRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        habitDialog = Dialog(this)

        settingsButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            var startIntent = Intent(applicationContext, SettingsActivity::class.java)
            startIntent.putExtra("com.nathanlee.habittracker.SOMETHING", "Text has changed")
            startActivity(startIntent)
        }

    }

    fun openHabitDialog(v: View) {
        var newDialog = HabitDialog()
        newDialog.show(supportFragmentManager, "Show com.nathanlee.habittracker.models.Habit")
    }

    fun showNewHabitPopup(v: View) {
        habitDialog.setContentView(R.layout.habit_popup)
        habitDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        colourRadioGroup = habitDialog.findViewById(R.id.colourGroup)
        colourRadioGroup.setOnCheckedChangeListener { _, colourId ->

            val habitNameTextView = habitDialog.findViewById<TextView>(R.id.habitNameText)
            habitNameTextView.setTextColor(selectColour(colourId))

        }

        val titleTextView = habitDialog.findViewById<TextView>(R.id.popupTitleText)
        titleTextView.setText(R.string.habit_popup_title_new)

        val habitNameEditText = habitDialog.findViewById<EditText>(R.id.habitNameText)
        habitNameEditText.setTextColor(selectColour(colourRadioGroup.checkedRadioButtonId))

        val cancelTextViewButton = habitDialog.findViewById<TextView>(R.id.cancelText)
        cancelTextViewButton.setOnClickListener {
            closeNewHabitPopup(cancelTextViewButton)
        }

        val saveTextViewButton = habitDialog.findViewById<TextView>(R.id.saveText)
        saveTextViewButton.setOnClickListener {
            closeNewHabitPopup(saveTextViewButton)
        }

        habitDialog.show()
    }

    fun closeNewHabitPopup(v: View) {
        if (v.id == R.id.saveText) {
            var selectedColour = selectColour(colourRadioGroup.checkedRadioButtonId)

            // TODO: Save habit info
        }

        habitDialog.dismiss()
    }

    fun selectColour(colourId: Int): Int {
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
                return ContextCompat.getColor(habitDialog.context, darkTheme[0])
            }
            R.id.blueRadio -> {
                return ContextCompat.getColor(habitDialog.context, darkTheme[1])
            }

            R.id.greenRadio -> {
                return ContextCompat.getColor(habitDialog.context, darkTheme[2])
            }

            R.id.orangeRadio -> {
                return ContextCompat.getColor(habitDialog.context, darkTheme[3])
            }

            R.id.pinkRadio -> {
                return ContextCompat.getColor(habitDialog.context, darkTheme[4])
            }

            R.id.purpleRadio -> {
                return ContextCompat.getColor(habitDialog.context, darkTheme[5])
            }

            R.id.yellowRadio -> {
                return ContextCompat.getColor(habitDialog.context, darkTheme[6])
            }
        }

        return ContextCompat.getColor(habitDialog.context, darkTheme[0])
    }
}
