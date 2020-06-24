package com.nathanlee.habittracker.components

import Completion
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
        var simpleDate: String = SimpleDateFormat("dd/MM/yyyy").format(managerDate)
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

        initializeHabits()
    }

    /*
    Initializes habits from the JSON
     */
    private fun initializeHabits() {
        rw = ReadWriteJson(filesDir.toString())

        if (rw.exists()) {
            habitList = rw.read()

            for (i in 0 until habitList.size) {
                val thisCompletionList = habitList[i].completions
                if (thisCompletionList.find(
                        0,
                        thisCompletionList.completions.size - 1,
                        todayDate
                    ) == -1
                ) {
                    val newCompletion = Completion(todayDate)
                    habitList[i].completions.edit(newCompletion, 1)
                }
            }
        } else {
            habitList = mutableListOf()
            rw.write(habitList)
        }
    }
}