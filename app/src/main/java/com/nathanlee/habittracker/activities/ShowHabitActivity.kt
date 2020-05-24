package com.nathanlee.habittracker.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.components.CalendarView
import com.nathanlee.habittracker.components.HabitManager
import com.nathanlee.habittracker.components.HabitManager.Companion.habitList
import com.nathanlee.habittracker.components.HabitManager.Companion.todayDate
import com.nathanlee.habittracker.components.StreakChartView
import com.nathanlee.habittracker.models.Habit
import components.ColourManager

//TODO: Add an overview section
//TODO: Notifications

class ShowHabitActivity : AppCompatActivity(), HabitDialog.HabitDialogListener {
    private lateinit var habit: Habit
    private var habitColour = 0
    private var habitIndex = 0
    private lateinit var calendarView: CalendarView
    private lateinit var streakChartView: StreakChartView
    private lateinit var layout: LinearLayout
    private lateinit var overviewText: TextView
    private lateinit var firstDayText: TextView
    private lateinit var totalText: TextView
    private lateinit var frequencyText: TextView
    private lateinit var notificationText: TextView
    private lateinit var calendarText: TextView
    private lateinit var streakText: TextView

    private lateinit var firstDayString: SpannableString
    private lateinit var totalString: SpannableString
    private lateinit var repeatString: SpannableString
    private lateinit var notificationString: SpannableString

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_habit)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.elevation = 0f
        actionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    baseContext,
                    R.color.dark_theme_actionbar
                )
            )
        )

        if (intent.hasExtra("habit_index")) {
            habitIndex = intent.extras!!.getInt("habit_index")
            habit = habitList[habitIndex]
            habitColour = ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
            actionBar!!.title = habit.name
        }

        layout = findViewById(R.id.chart_layout)

        overviewText = findViewById(R.id.overview_text)
        overviewText.setTextColor(habitColour)

        var overviewTextColour = ContextCompat.getColor(baseContext, R.color.dark_theme_title)

        firstDayText = findViewById(R.id.overview_first_day)
        var firstCompletion = if (habit.streaks.streaks.isEmpty()){
            "Not started"
        } else {
            "${habit.streaks.streaks[0].start.toString()}"
        }
        var firstDayStringValue = SpannableString(firstCompletion)
        firstDayStringValue.setSpan(ForegroundColorSpan(habitColour), 0, firstDayStringValue.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        firstDayString = SpannableString(getString(R.string.show_habit_first_day))
        firstDayString.setSpan(overviewTextColour, 0, firstDayString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        firstDayText.text = firstDayStringValue
        firstDayText.append(firstDayString)

        totalText = findViewById(R.id.overview_total)
        var totalStringValue = SpannableString("${habit.completions.total}")
        totalStringValue.setSpan(ForegroundColorSpan(habitColour), 0, totalStringValue.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        totalString = SpannableString(getString(R.string.show_habit_total))
        totalString.setSpan(overviewTextColour, 0, totalString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        totalText.setText(totalStringValue, TextView.BufferType.SPANNABLE)
        totalText.append(totalString)

        frequencyText = findViewById(R.id.overview_frequency)
        var frequencyString = SpannableString("${habit.numerator} time(s) in ${habit.denominator} day(s)")
        frequencyString.setSpan(ForegroundColorSpan(habitColour), 0, frequencyString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        repeatString = SpannableString(getString(R.string.show_habit_repeat))
        repeatString.setSpan(overviewTextColour, 0, repeatString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        frequencyText.setText(frequencyString, TextView.BufferType.SPANNABLE)
        frequencyText.append(repeatString)

        notificationText= findViewById(R.id.overview_notification)
        var notificationStringValue = SpannableString("${habit.completions.total}")
        notificationStringValue.setSpan(ForegroundColorSpan(habitColour), 0, notificationStringValue.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        notificationString = SpannableString(getString(R.string.show_habit_notification))
        notificationString.setSpan(overviewTextColour, 0, notificationString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        notificationText.setText(notificationStringValue, TextView.BufferType.SPANNABLE)
        notificationText.append(notificationString)

        calendarView = findViewById(R.id.calendar)
        calendarView.habitIndex = habitIndex
        calendarView.showHabitActivity = this
        calendarView.updateCalendar(todayDate)

        calendarText = findViewById(R.id.history_text)
        calendarText.setTextColor(habitColour)

        val chart: BarChart = findViewById(R.id.streak_chart)
        streakChartView = StreakChartView(habitIndex, chart, this@ShowHabitActivity)
        streakChartView.createGraph(layout)

        streakText = findViewById(R.id.streak_text)
        streakText.setTextColor(habitColour)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.show_habit, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                val homeIntent = Intent(this, MainActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(homeIntent)
            }
            R.id.edit_habit_app_bar -> {
                openHabitDialog()
            }
            R.id.delete_habit_app_bar -> {
                openConfirmationDialog()
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    /*
    Retrieves habit info after creating a new habit through the dialog box
     */
    override fun sendHabit(habit: Habit) {
        habitList[habitIndex].replace(
            habit.name,
            habit.description,
            habit.colour,
            habit.numerator,
            habit.denominator
        )
        habitList[habitIndex].updatePeriodCompletions(habitList[habitIndex].completions.completions.first().timestamp)
        habitList[habitIndex].updatePeriodStreak(habitList[habitIndex].completions.completions.first().timestamp)
        HabitManager.rw.write(habitList)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title = habit.name
        calendarView.updateCalendar(todayDate)
        habitColour = ColourManager.selectColour(
            habitList[habitIndex].colour,
            baseContext
        )
        updateStats()
    }

    /*
    Opens the dialog to create a new habit
     */
    private fun openHabitDialog() {
        var newDialog = HabitDialog(false, habit)
        newDialog.show(supportFragmentManager, "Show models.Habit")
    }

    private fun openConfirmationDialog() {
        var newDialog = ConfirmationDialog(this)
        newDialog.show(supportFragmentManager, "Show models.Habit")
    }

    private fun updateStats() {
        overviewText.setTextColor(habitColour)

        frequencyText = findViewById(R.id.overview_frequency)
        var frequencyString = SpannableString("${habit.numerator} time(s) in ${habit.denominator} day(s)")
        frequencyString.setSpan(ForegroundColorSpan(habitColour), 0, frequencyString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        frequencyText.setText(frequencyString, TextView.BufferType.SPANNABLE)
        frequencyText.append(repeatString)

        notificationText= findViewById(R.id.overview_notification)
        var notificationStringValue = SpannableString("${habit.completions.total}")
        notificationStringValue.setSpan(ForegroundColorSpan(habitColour), 0, notificationStringValue.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        notificationText.setText(notificationStringValue, TextView.BufferType.SPANNABLE)
        notificationText.append(notificationString)

        calendarText.setTextColor(habitColour)
        streakText.setTextColor(habitColour)

        updateCompletionText()
    }

    fun updateCompletionText() {
        firstDayText = findViewById(R.id.overview_first_day)
        var firstCompletion = if (habit.streaks.streaks.isEmpty()){
            "Not started"
        } else {
            "${habit.streaks.streaks[0].start.toString()}"
        }
        var firstDayStringValue = SpannableString(firstCompletion)
        firstDayStringValue.setSpan(ForegroundColorSpan(habitColour), 0, firstDayStringValue.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        firstDayText.text = firstDayStringValue
        firstDayText.append(firstDayString)

        totalText = findViewById(R.id.overview_total)
        var totalStringValue = SpannableString("${habit.completions.total}")
        totalStringValue.setSpan(ForegroundColorSpan(habitColour), 0, totalStringValue.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        totalText.setText(totalStringValue, TextView.BufferType.SPANNABLE)
        totalText.append(totalString)

        streakChartView.refreshColour()
        streakChartView.updateGraph(layout)
    }

    fun delete() {
        if (!HabitManager.editLock) {
            HabitManager.editLock = true
            habitList.removeAt(habitIndex)
            HabitManager.rw.write(habitList)
            HabitManager.editLock = false
        }

        var startIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(startIntent)
    }
}
