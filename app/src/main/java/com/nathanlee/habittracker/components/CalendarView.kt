package com.nathanlee.habittracker.components

import Timestamp
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var header: LinearLayout
    private lateinit var previousButton: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var txtDisplayDate: TextView
    private lateinit var todayButton: Button
    private lateinit var gridView: GridView
    private lateinit var today: Timestamp
    private lateinit var displayDate: Timestamp
    var habitIndex: Int = 0

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(com.nathanlee.habittracker.R.layout.calendar_layout, this)
        findUIViews()
        initializeCalendar()
        initializeButtons()
    }

    private fun findUIViews() {
        header = findViewById(com.nathanlee.habittracker.R.id.date_display_linear_layout)
        previousButton = findViewById(com.nathanlee.habittracker.R.id.calendar_previous_button)
        nextButton = findViewById(com.nathanlee.habittracker.R.id.calendar_next_button)
        txtDisplayDate = findViewById(com.nathanlee.habittracker.R.id.date_display_month)
        todayButton = findViewById(com.nathanlee.habittracker.R.id.date_display_today)
        gridView = findViewById(com.nathanlee.habittracker.R.id.calendar_grid)
    }

    private fun initializeCalendar() {
        val date = Date()
        val simpleDate = SimpleDateFormat("dd/MM/yyyy").format(date)
        today = Timestamp(simpleDate)
        displayDate = today
        updateCalendar(displayDate)
    }

    /*
     * Display dates correctly in grid
     */
    fun updateCalendar(date: Timestamp) {
        // update grid
        gridView.adapter = CalendarAdapter(
            context,
            com.nathanlee.habittracker.R.layout.calendar_grid_layout,
            habitIndex,
            date,
            this
        )

        txtDisplayDate.text = date.monthString(date)
    }

    private fun initializeButtons() {
        nextButton.setOnClickListener {
            displayDate = displayDate.getNextMonth(displayDate)
            if (displayDate.compareTo(today) != 1) {
                updateCalendar(displayDate)
            } else {
                displayDate = today
            }
        }

        previousButton.setOnClickListener {
            displayDate = displayDate.getPreviousMonth(displayDate)
            updateCalendar(displayDate)
        }

        todayButton.setOnClickListener {
            displayDate = today
            updateCalendar(displayDate)
        }
    }
}