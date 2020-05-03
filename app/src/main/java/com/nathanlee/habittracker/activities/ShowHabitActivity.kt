package com.nathanlee.habittracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.nathanlee.habittracker.components.CalendarView
import com.nathanlee.habittracker.components.HabitManager.Companion.habitList
import com.nathanlee.habittracker.models.Habit

class ShowHabitActivity : AppCompatActivity() {

    private lateinit var habit: Habit
    private var habitIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nathanlee.habittracker.R.layout.activity_show_habit)

        val actionBar = supportActionBar
        actionBar!!.title = "Habit"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("habit_index")) {
            habitIndex = intent.extras!!.getInt("habit_index")
            habit = habitList[habitIndex]
            actionBar!!.title = habit.name
        }

    }

    override fun onResume() {
        super.onResume()
        val calendarView = findViewById<CalendarView>(com.nathanlee.habittracker.R.id.calendar)
        calendarView.habitIndex = habitIndex
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.nathanlee.habittracker.R.menu.show_habit, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                val homeIntent = Intent(this, MainActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(homeIntent)
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }
}
