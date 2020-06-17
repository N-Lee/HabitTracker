package com.nathanlee.habittracker.components

import ReadWriteJson
import Timestamp
import android.app.Application
import android.content.Context
import com.nathanlee.habittracker.models.Habit
import java.text.SimpleDateFormat
import java.util.*

class HabitManager: Application() {

    init {
        instance = this
    }

    companion object{
        private var instance: HabitManager? = null
        lateinit var habitList: MutableList<Habit>
        lateinit var rw: ReadWriteJson
        var managerDate = Date()
        var simpleDate = SimpleDateFormat("dd/MM/yyyy").format(managerDate)
        var todayDate = Timestamp(simpleDate)
        var editLock = false
        var nextId = 0

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext()
    }
}