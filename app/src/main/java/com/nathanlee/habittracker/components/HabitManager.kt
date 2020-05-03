package com.nathanlee.habittracker.components

import ReadWriteJson
import android.app.Application
import com.nathanlee.habittracker.models.Habit

class HabitManager: Application() {
    companion object{
        lateinit var habitList: MutableList<Habit>
        lateinit var rw: ReadWriteJson
    }
}