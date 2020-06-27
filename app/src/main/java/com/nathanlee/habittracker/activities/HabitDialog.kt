package com.nathanlee.habittracker.activities

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.components.HabitManager.Companion.habitList
import com.nathanlee.habittracker.components.HabitManager.Companion.nextId
import com.nathanlee.habittracker.models.Habit
import components.ColourManager
import components.ColourManager.colourIdToString
import components.ColourManager.selectColour
import java.text.SimpleDateFormat
import java.util.*

class HabitDialog(isNew: Boolean, habitIndex: Int) : AppCompatDialogFragment() {
    private val isNew = isNew
    private val habitIndex = habitIndex
    private lateinit var habit: Habit
    private lateinit var habitActivity: ShowHabitActivity
    private lateinit var mainActivity: MainActivity
    private lateinit var colourRadioGroup: RadioGroup
    private lateinit var titleTextView: TextView
    private lateinit var habitNameEditText: EditText
    private lateinit var habitDescriptionEditTextView: EditText
    private lateinit var habitNumeratorEditTextView: EditText
    private lateinit var habitDenominatorEditText: EditText
    private lateinit var habitNotificationSwitch: Switch
    private lateinit var habitNotificationTimeTextView: TextView
    private lateinit var sundayCheckBox: CheckBox
    private lateinit var mondayCheckBox: CheckBox
    private lateinit var tuesdayCheckBox: CheckBox
    private lateinit var wednesdayCheckBox: CheckBox
    private lateinit var thursdayCheckBox: CheckBox
    private lateinit var fridayCheckBox: CheckBox
    private lateinit var saturdayCheckBox: CheckBox
    private lateinit var cancelTextView: TextView
    private lateinit var saveTextView: TextView
    private lateinit var listener: HabitDialogListener
    private lateinit var error: Toast

    constructor (isNew: Boolean, habitIndex: Int, habitActivity: ShowHabitActivity) : this(isNew, habitIndex) {
        habit = habitList[habitIndex]
        this.habitActivity = habitActivity
    }

    constructor (isNew: Boolean, habitIndex: Int, mainActivity: MainActivity) : this(isNew, habitIndex) {
        this.mainActivity = mainActivity
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
        habitNotificationSwitch = view.findViewById(R.id.notification_switch)
        habitNotificationTimeTextView = view.findViewById(R.id.notification_time)
        var notificationCheckBoxLayout =
            view.findViewById<LinearLayout>(R.id.notification_checkbox_layout)
        sundayCheckBox = view.findViewById(R.id.notification_sunday_checkbox)
        mondayCheckBox = view.findViewById(R.id.notification_monday_checkbox)
        tuesdayCheckBox = view.findViewById(R.id.notification_tuesday_checkbox)
        wednesdayCheckBox = view.findViewById(R.id.notification_wednesday_checkbox)
        thursdayCheckBox = view.findViewById(R.id.notification_thursday_checkbox)
        fridayCheckBox = view.findViewById(R.id.notification_friday_checkbox)
        saturdayCheckBox = view.findViewById(R.id.notification_saturday_checkbox)

        if (isNew) {
            titleTextView.setText(R.string.habit_popup_title_new)
        } else {
            habitNameEditText.setText(habit.name)
            habitDescriptionEditTextView.setText(habit.description)
            habitDenominatorEditText.setText(habit.denominator.toString())
            habitNumeratorEditTextView.setText(habit.numerator.toString())
            colourRadioGroup.check(ColourManager.colourStringToID(habit.colour))
            habitNotificationSwitch.isChecked = habit.notification
            habitNotificationTimeTextView.text = habit.notificationTime
            sundayCheckBox.isChecked = habit.notificationDays[0]
            mondayCheckBox.isChecked = habit.notificationDays[1]
            tuesdayCheckBox.isChecked = habit.notificationDays[2]
            wednesdayCheckBox.isChecked = habit.notificationDays[3]
            thursdayCheckBox.isChecked = habit.notificationDays[4]
            fridayCheckBox.isChecked = habit.notificationDays[5]
            saturdayCheckBox.isChecked = habit.notificationDays[6]
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

        habitNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (habitNotificationSwitch.isChecked) {
                habitNotificationTimeTextView.visibility = View.VISIBLE
                notificationCheckBoxLayout.visibility = View.VISIBLE
            } else {
                habitNotificationTimeTextView.visibility = View.GONE
                notificationCheckBoxLayout.visibility = View.GONE
            }
        }

        if (habitNotificationSwitch.isChecked) {
            habitNotificationTimeTextView.visibility = View.VISIBLE
            notificationCheckBoxLayout.visibility = View.VISIBLE
        } else {
            habitNotificationTimeTextView.visibility = View.GONE
            notificationCheckBoxLayout.visibility = View.GONE
        }

        val cal = Calendar.getInstance()
        habitNotificationTimeTextView.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                habitNotificationTimeTextView.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

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

    /*
    Closes the dialog and creates the new habit
     */
    private fun closeNewHabitPopup(v: View) {
        if (v.id == R.id.save_text) {
            var colour = colourIdToString(colourRadioGroup.checkedRadioButtonId)
            var name = habitNameEditText.text.toString()
            var description = habitDescriptionEditTextView.text.toString()
            var numerator = habitNumeratorEditTextView.text.toString().toInt()
            var denominator = habitDenominatorEditText.text.toString().toInt()
            var notification = habitNotificationSwitch.isChecked
            var notificationTime = habitNotificationTimeTextView.text.toString()
            var notificationDays = booleanArrayOf(
                sundayCheckBox.isChecked,
                mondayCheckBox.isChecked,
                tuesdayCheckBox.isChecked,
                wednesdayCheckBox.isChecked,
                thursdayCheckBox.isChecked,
                fridayCheckBox.isChecked,
                saturdayCheckBox.isChecked
            )

            if (numerator > denominator) {
                error = Toast.makeText(
                    requireContext(),
                    "First number cannot be greater than the second number",
                    Toast.LENGTH_SHORT
                )
                error.show()
                return
            } else if (numerator <= 0 || denominator <= 0) {
                error = Toast.makeText(
                    requireContext(),
                    "The numbers must be greater than 0",
                    Toast.LENGTH_SHORT
                )
                error.show()
                return
            } else if (numerator == denominator) {
                numerator = 1
                denominator = 1
            }

            var dayOfWeekFilled = false
            dayOfWeek@ for (i in notificationDays) {
                if (i){
                    dayOfWeekFilled = true
                    break@dayOfWeek
                }
            }

            if (!dayOfWeekFilled){
                notification = false
            }

            var habit = Habit(
                name,
                description,
                colour,
                numerator,
                denominator,
                nextId,
                notification,
                notificationTime,
                notificationDays
            )

            listener.sendHabit(habit)

            if (this::habitActivity.isInitialized) {
                habitActivity.notificationPopUp(
                    notification,
                    notificationDays,
                    notificationTime
                )
            } else {
                mainActivity.notificationPopUp(
                    notification,
                    notificationDays,
                    notificationTime,
                    habitIndex
                )
            }

        }

        this.dismiss()
    }

    interface HabitDialogListener {
        fun sendHabit(habit: Habit)
    }
}