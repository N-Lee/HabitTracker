package com.nathanlee.habittracker.components

import Completion
import Timestamp
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.nathanlee.habittracker.components.HabitManager.Companion.habitList
import com.nathanlee.habittracker.components.HabitManager.Companion.rw

class CalendarAdapter(
    var context: Context,
    var resource: Int,
    var habitIndex: Int,
    var date: Timestamp,
    var calendarView: CalendarView
) :
    BaseAdapter() {
    private var days: MutableList<Completion> = getCompletions(date)

    override fun getItem(position: Int): Any {
        return days[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return days.size
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater =
            parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(resource, parent, false)

        var dateText = view.findViewById<TextView>(com.nathanlee.habittracker.R.id.calendar_date)
        val item =
            view.findViewById<LinearLayout>(com.nathanlee.habittracker.R.id.calendar_item_layout)

        val dayTimestamp = days!![position].timestamp
        var completionStatus = days!![position].status

        if (dayTimestamp.dayInt.toString() == "0") {
            dateText.text = ""
        } else {
            dateText.text = dayTimestamp.dayInt.toString()
        }

        setColour(completionStatus, item)

        item.setOnClickListener {
            when (completionStatus) {
                2,3 -> {
                    habitList[habitIndex].editDate(dayTimestamp, 0)
                }
                else -> {
                    habitList[habitIndex].editDate(dayTimestamp, 2)
                }
            }

            completionStatus = days!![position].status
            rw.write(habitList)
            calendarView.updateCalendar(dayTimestamp)

        }

        return view
    }

    private fun setColour(completionStatus: Int, view: View?){
        when (completionStatus) {
            1 -> {
                view!!.setBackgroundColor(
                    ColourManager.selectPartialCompleteColour(
                        habitList[habitIndex].colour, context
                    )
                )
            }
            2,3 -> {
                view!!.setBackgroundColor(ColourManager.selectColour(
                    habitList[habitIndex].colour, context
                ))
            }
            else -> {
                view!!.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun getCompletions(today: Timestamp): MutableList<Completion> {
        val completions = habitList[habitIndex].completions
        var index = completions.find(0, completions.completions.size, today)
        val monthLength = today.monthLength(today)
        val dayInt = today.dayInt

        var daysSinceFirstDayInMonth = dayInt - 1
        val daysUntilLastDayInMonth = monthLength - dayInt

        var firstDayIndex = index - daysSinceFirstDayInMonth

        if (firstDayIndex < 0) {
            val firstOfMonth = today.getDaysBefore(daysSinceFirstDayInMonth).date
            habitList[habitIndex].editDate(Timestamp(firstOfMonth), 0)
            firstDayIndex = 0
        }

        index = completions.find(0, completions.completions.size, today)
        var lastDayIndex = index + daysUntilLastDayInMonth

        if (lastDayIndex >= completions.completions.size) {
            lastDayIndex = habitList[habitIndex].completions.completions.size
        }

        val firstDayOfWeek =
            today.getDayOfWeekIndex(completions.completions[firstDayIndex].timestamp)
        days = mutableListOf()

        while (days.size < firstDayOfWeek) {
            val emptyDate = Timestamp("00/00/0000")
            days.add(Completion(emptyDate))
        }

        for (i in firstDayIndex..lastDayIndex) {
            if (i < completions.completions.size) {
                days.add(completions.completions[i])
            } else {
                return days
            }
        }


        return days
    }
}