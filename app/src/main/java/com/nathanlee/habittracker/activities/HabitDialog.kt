package com.nathanlee.habittracker.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.models.Habit
import components.ColourManager
import components.ColourManager.colourIdToString
import components.ColourManager.selectColour

class HabitDialog(isNew: Boolean) : AppCompatDialogFragment() {
    private val isNew = isNew
    private lateinit var habit: Habit
    private lateinit var colourRadioGroup: RadioGroup
    private lateinit var titleTextView: TextView
    private lateinit var habitNameEditText: EditText
    private lateinit var habitDescriptionEditTextView: EditText
    private lateinit var habitNumeratorEditTextView: EditText
    private lateinit var habitDenominatorEditText: EditText
    private lateinit var cancelTextView: TextView
    private lateinit var saveTextView: TextView
    private lateinit var listener: HabitDialogListener
    private lateinit var error: Toast

    constructor (isNew: Boolean, habit: Habit): this(isNew){
        this.habit = habit
    }

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
        val view = inflater.inflate(R.layout.habit_popup, null)

        builder.setView(view)

        colourRadioGroup = view.findViewById(R.id.colour_group)

        titleTextView = view.findViewById(R.id.popup_title_text)
        habitNameEditText = view.findViewById(R.id.habit_name_text)
        habitDescriptionEditTextView =
            view.findViewById(R.id.habit_description_text)
        habitNumeratorEditTextView =
            view.findViewById(R.id.habit_numerator_edit_text)
        habitDenominatorEditText =
            view.findViewById(R.id.habit_denominator_edit_text)
        if (isNew){
            titleTextView.setText(R.string.habit_popup_title_new)
        } else {
            habitNameEditText.setText(habit.name)
            habitDescriptionEditTextView.setText(habit.description)
            habitDenominatorEditText.setText(habit.denominator.toString())
            habitNumeratorEditTextView.setText(habit.numerator.toString())
            colourRadioGroup.check(ColourManager.colourStringToID(habit.colour))
        }

        colourRadioGroup.setOnCheckedChangeListener { _, colourId ->
            val colour = colourIdToString(colourId)
            habitNameEditText.setTextColor(selectColour(colour, requireContext()))
        }

        val colour = colourIdToString(colourRadioGroup.checkedRadioButtonId)
        habitNameEditText.setTextColor(
            selectColour(
                colour,
                requireContext()
            )
        )

        cancelTextView = view.findViewById(R.id.cancel_text)
        cancelTextView.setOnClickListener {
            closeNewHabitPopup(cancelTextView)
        }

        saveTextView = view.findViewById(R.id.save_text)
        saveTextView.setOnClickListener {
            closeNewHabitPopup(saveTextView)
        }

        error = Toast.makeText(requireContext(), null, Toast.LENGTH_LONG)

        return builder.create()
    }

    private fun closeNewHabitPopup(v: View) {
        if (v.id == R.id.save_text) {
            var colour = colourIdToString(colourRadioGroup.checkedRadioButtonId)
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
            } else if (numerator == denominator){
                numerator = 1
                denominator = 1
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