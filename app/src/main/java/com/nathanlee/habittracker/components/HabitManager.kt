package com.nathanlee.habittracker.components

import ReadWriteJson
import Timestamp
import android.app.Application
import com.nathanlee.habittracker.models.Habit
import java.text.SimpleDateFormat
import java.util.*

class HabitManager: Application() {
    companion object{
        lateinit var habitList: MutableList<Habit>
        lateinit var rw: ReadWriteJson
        val date = Date()
        private val simpleDate = SimpleDateFormat("dd/MM/yyyy").format(date)
        val todayDate = Timestamp(simpleDate)
        var editLock = false
    }
}