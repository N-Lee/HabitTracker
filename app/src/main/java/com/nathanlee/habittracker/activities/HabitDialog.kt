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
import com.nathanlee.habittracker.components.ColourManager.colourIdToString
import com.nathanlee.habittracker.components.ColourManager.selectColour
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

        habitNameTextView = view.findViewById(com.nathanlee.habittracker.R.id.habit_name_text)

        colourRadioGroup = view.findViewById(com.nathanlee.habittracker.R.id.colour_group)
        colourRadioGroup.setOnCheckedChangeListener { _, colourId ->
            val colour = colourIdToString(colourId, requireContext())
            habitNameTextView.setTextColor(selectColour(colour, requireContext()))
        }

        titleTextView = view.findViewById(com.nathanlee.habittracker.R.id.popup_title_text)
        titleTextView.setText(com.nathanlee.habittracker.R.string.habit_popup_title_new)

        habitNameEditText = view.findViewById(com.nathanlee.habittracker.R.id.habit_name_text)
        val colour = colourIdToString(colourRadioGroup.checkedRadioButtonId, requireContext())
        habitNameEditText.setTextColor(
            selectColour(
                colour,
                requireContext()
            )
        )

        habitDescriptionEditTextView =
            view.findViewById(com.nathanlee.habittracker.R.id.habit_description_text)

        habitNumeratorEditTextView =
            view.findViewById(com.nathanlee.habittracker.R.id.habit_numerator_edit_text)
        habitDenominatorEditText =
            view.findViewById(com.nathanlee.habittracker.R.id.habit_denominator_edit_text)

        cancelTextView = view.findViewById(com.nathanlee.habittracker.R.id.cancel_text)
        cancelTextView.setOnClickListener {
            closeNewHabitPopup(cancelTextView)
        }

        saveTextView = view.findViewById(com.nathanlee.habittracker.R.id.save_text)
        saveTextView.setOnClickListener {
            closeNewHabitPopup(saveTextView)
        }

        error = Toast.makeText(requireContext(), null, Toast.LENGTH_LONG)

        return builder.create()
    }

    fun closeNewHabitPopup(v: View) {
        if (v.id == com.nathanlee.habittracker.R.id.save_text) {
            var colour = colourIdToString(colourRadioGroup.checkedRadioButtonId, requireContext())
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