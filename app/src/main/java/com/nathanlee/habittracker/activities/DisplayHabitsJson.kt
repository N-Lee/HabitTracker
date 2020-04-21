package com.nathanlee.habittracker.activities

import ReadWriteJson
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nathanlee.habittracker.R

class DisplayHabitsJson : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_habits_json)

        var rw = ReadWriteJson(filesDir.toString())
        var jsonTextView = findViewById<TextView>(R.id.json)
        jsonTextView.text = rw.display()
    }
}
