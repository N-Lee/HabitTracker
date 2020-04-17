package com.nathanlee.habittracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.nathanlee.habittracker.models.Habit

class EditHabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nathanlee.habittracker.R.layout.activity_edit_habit)

        val actionBar = supportActionBar
        actionBar!!.title = "Habit"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        if (getIntent().hasExtra("edit_habit")) {
            var habit: Habit = getIntent().getParcelableExtra("edit_habit")
            actionBar!!.title = habit.name
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.nathanlee.habittracker.R.menu.main, menu)

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
