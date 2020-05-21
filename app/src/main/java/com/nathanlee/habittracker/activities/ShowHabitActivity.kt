package com.nathanlee.habittracker.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
    private var habitIndex = 0
    private lateinit var calendarView: CalendarView
    private lateinit var streakChartView: StreakChartView
    private lateinit var layout: LinearLayout
    private lateinit var calendarText: TextView
    private lateinit var streakText: TextView

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
            actionBar!!.title = habit.name
        }

        layout = findViewById(R.id.chart_layout)

        val chart: BarChart = findViewById(R.id.streak_chart)
        streakChartView = StreakChartView(habitIndex, chart, this@ShowHabitActivity)
        streakChartView.createGraph(layout)

        calendarView = findViewById(R.id.calendar)
        calendarView.habitIndex = habitIndex
        calendarView.showHabitActivity = this
        calendarView.updateCalendar(todayDate)

        calendarText = findViewById(R.id.history_text)
        calendarText.setTextColor(
            ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
        )

        streakText = findViewById(R.id.streak_text)
        streakText.setTextColor(
            ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
        )

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

    fun updateStats() {
        streakText.setTextColor(
            ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
        )
        calendarText.setTextColor(
            ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
        )

        streakChartView.refreshColour()
        streakChartView.updateGraph(layout)
    }

    fun delete(){
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
