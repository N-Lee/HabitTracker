package com.nathanlee.habittracker.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nathanlee.habittracker.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (getIntent().hasExtra("com.nathanlee.habittracker.SOMETHING")) {
            var tv: TextView = findViewById(R.id.textView)
            var text: String = getIntent().getStringExtra("com.nathanlee.habittracker.SOMETHING")
            tv.setText(text)
        }
    }
}
