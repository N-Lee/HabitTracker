package com.nathanlee.habittracker.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.nathanlee.habittracker.models.ColourManager.selectColour
import com.nathanlee.habittracker.models.Habit

class HabitDialog : AppCompatDialogFragment() {
    private lateinit var colourRadioGroup: RadioGroup
    private lateinit var habitNameTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var habitNameEditText: EditText
    private lateinit var habitDescriptionEditTextView: EditText
    private lateinit var habitNumeratorEditTextView: EditText
    private lateinit var habitDenominatorEditText: EditText
    private lateinit var cancelTextView: TextView
    private lateinit var saveTextView: TextView
    private lateinit var listener: HabitDialogListener
    private lateinit var error: Toast

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            listener = context as HabitDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement HabitDialogListener")
        } catch (e: Exception) {
            throw Exception("Error")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(com.nathanlee.habittracker.R.layout.habit_popup, null)

        builder.setView(view)

        habitNameTextView = view.findViewById(com.nathanlee.habittracker.R.id.habitNameText)

        colourRadioGroup = view.findViewById(com.nathanlee.habittracker.R.id.colourGroup)
        colourRadioGroup.setOnCheckedChangeListener { _, colourId ->
            habitNameTextView.setTextColor(selectColour(colourId, requireContext()))
        }

        titleTextView = view.findViewById(com.nathanlee.habittracker.R.id.popupTitleText)
        titleTextView.setText(com.nathanlee.habittracker.R.string.habit_popup_title_new)

        habitNameEditText = view.findViewById(com.nathanlee.habittracker.R.id.habitNameText)
        habitNameEditText.setTextColor(
            selectColour(
                colourRadioGroup.checkedRadioButtonId,
                requireContext()
            )
        )

        habitDescriptionEditTextView =
            view.findViewById(com.nathanlee.habittracker.R.id.habitDescriptionText)

        habitNumeratorEditTextView =
            view.findViewById(com.nathanlee.habittracker.R.id.habitNumeratorEditText)
        habitDenominatorEditText =
            view.findViewById(com.nathanlee.habittracker.R.id.habitDenominatorEditText)

        cancelTextView = view.findViewById(com.nathanlee.habittracker.R.id.cancelText)
        cancelTextView.setOnClickListener {
            closeNewHabitPopup(cancelTextView)
        }

        saveTextView = view.findViewById(com.nathanlee.habittracker.R.id.saveText)
        saveTextView.setOnClickListener {
            closeNewHabitPopup(saveTextView)
        }

        error = Toast.makeText(requireContext(), null, Toast.LENGTH_LONG)

        return builder.create()
    }

    fun closeNewHabitPopup(v: View) {
        if (v.id == com.nathanlee.habittracker.R.id.saveText) {
            var colour = colourRadioGroup.checkedRadioButtonId
            var name = habitNameEditText.text.toString()
            var description = habitDescriptionEditTextView.text.toString()
            var numerator = habitNumeratorEditTextView.text.toString().toInt()
            var denominator = habitDenominatorEditText.text.toString().toInt()

            if (numerator > denominator) {
                error = Toast.makeText(
                        requireContext(),
                        "First number cannot be greater than the second number",
                        Toast.LENGTH_SHORT
                    )
                error.show()
                return
            }

            var habit = Habit(name, description, colour, numerator, denominator)
            listener.sendHabit(habit)
        }

        this.dismiss()

    }

    interface HabitDialogListener {
        fun sendHabit(habit: Habit)
    }
}