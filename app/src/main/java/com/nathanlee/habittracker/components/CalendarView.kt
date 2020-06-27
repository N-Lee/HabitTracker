package com.nathanlee.habittracker.components

import Completion
import Timestamp
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.activities.ShowHabitActivity
import com.nathanlee.habittracker.components.HabitManager.Companion.editLock
import com.nathanlee.habittracker.components.HabitManager.Companion.todayDate

class CalendarView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    CalendarAdapter.OnDateListener {
    private lateinit var header: LinearLayout
    private lateinit var previousButton: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var textDisplayMonth: TextView
    private lateinit var textDisplayYear: TextView
    private lateinit var todayButton: Button
    private lateinit var displayDate: Timestamp
    private lateinit var recyclerView: RecyclerView
    private lateinit var days: MutableList<Completion>
    private lateinit var calendarAdapter: CalendarAdapter
    lateinit var showHabitActivity: ShowHabitActivity
    var habitIndex: Int = 0

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.calendar_layout, this)
        findUIViews()
        initializeCalendar()
        initializeButtons()
    }

    /*
    Toggles between whether the habit has been completed on tapped date
     */
    override fun onDateClick(position: Int) {
        if (!editLock) {
            editLock = true
            val dayTimestamp = days!![position].timestamp
            var completionStatus = days!![position].status

            if (dayTimestamp.compareTo(Timestamp("00/00/0000")) != 0) {
                when (completionStatus) {
                    2, 3 -> {
                        HabitManager.habitList[habitIndex].editDate(dayTimestamp, 0)
                    }
                    else -> {
                        HabitManager.habitList[habitIndex].editDate(dayTimestamp, 2)
                    }
                }

                HabitManager.rw.write(HabitManager.habitList)
                updateCalendar(dayTimestamp)
                showHabitActivity.updateCompletionText()
            }
            editLock = false
        }
    }

    /*
 Find all the UI views
 */
    private fun findUIViews() {
        header = findViewById(R.id.date_display_linear_layout)
        previousButton = findViewById(R.id.calendar_previous_button)
        nextButton = findViewById(R.id.calendar_next_button)
        textDisplayMonth = findViewById(R.id.date_display_month)
        textDisplayYear = findViewById(R.id.date_display_year)
        todayButton = findViewById(R.id.date_display_today)
        recyclerView = findViewById(R.id.calendar_recycler)
    }

    /*
    Updates the values for the calendar when activity opens
     */
    private fun initializeCalendar() {
        displayDate = todayDate
        days = getCompletions(displayDate)

        val manager = GridLayoutManager(context, 7)
        recyclerView.layoutManager = manager
        calendarAdapter = CalendarAdapter(
            context,
            habitIndex,
            days,
            this
        )
        recyclerView.adapter = calendarAdapter
    }

    /*
    Updates the calendar text when changing months
     */
    fun updateCalendar(date: Timestamp) {
        val year = date.yearInt
        val month = date.monthString(date)
        textDisplayMonth.text = "$month"
        textDisplayYear.text = "$year"

        days = getCompletions(date)
        calendarAdapter.days = days
        calendarAdapter.habitIndex = habitIndex
        calendarAdapter.notifyDataSetChanged()
    }

    /*
    Gets the completions for the month that is displayed
     */
    private fun getCompletions(today: Timestamp): MutableList<Completion> {
        val emptyDate = Timestamp("00/00/0000")
        val completions = HabitManager.habitList[habitIndex].completions
        var index = completions.find(0, completions.completions.size, today)
        val monthLength = today.monthLength(today)
        val dayInt = today.dayInt

        var daysSinceFirstDayInMonth = dayInt - 1
        val daysUntilLastDayInMonth = monthLength - dayInt

        var firstDayIndex = index - daysSinceFirstDayInMonth

        if (firstDayIndex < 0) {
            val firstOfMonth = today.getDaysBefore(daysSinceFirstDayInMonth).date
            HabitManager.habitList[habitIndex].editDate(Timestamp(firstOfMonth), 0)
            firstDayIndex = 0
        }

        index = completions.find(0, completions.completions.size, today)
        var lastDayIndex = index + daysUntilLastDayInMonth

        if (lastDayIndex >= completions.completions.size) {
            lastDayIndex = HabitManager.habitList[habitIndex].completions.completions.size
        }

        val firstDayOfWeek =
            today.getDayOfWeekIndex(completions.completions[firstDayIndex].timestamp)
        var days = mutableListOf<Completion>()

        while (days.size < firstDayOfWeek) {
            days.add(Completion(emptyDate))
        }

        for (i in firstDayIndex..lastDayIndex) {
            if (i < completions.completions.size) {
                days.add(completions.completions[i])
            } else {
                break
            }
        }

        while (days.size < 35) {
            days.add(Completion(emptyDate))
        }

        return days
    }

    /*
    Sets the listeners for each button
     */
    private fun initializeButtons() {
        nextButton.setOnClickListener {
            displayDate = displayDate.getNextMonth(displayDate)
            if (displayDate.compareTo(todayDate) != 1) {
                updateCalendar(displayDate)
            } else {
                displayDate = todayDate
            }
        }

        previousButton.setOnClickListener {
            displayDate = displayDate.getPreviousMonth(displayDate)
            updateCalendar(displayDate)
        }

        todayButton.setOnClickListener {
            displayDate = todayDate
            updateCalendar(displayDate)
        }
    }
}