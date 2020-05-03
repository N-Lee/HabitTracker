package com.nathanlee.habittracker.models

import ReadWriteJson
import Timestamp
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class HabitTest {

    @Test
    fun updateAllCompletions() {

        var today = Timestamp("31/03/2021")

        val rw = ReadWriteJson("src/main/java/com/nathanlee/habittracker/files")
        val test = rw.read()
        var habit = test[0]
        //var completionsList = habit.getCompletionsInMonth(today)



        val date = Date()
        val simpleDate = SimpleDateFormat("dd/MM/yyyy").format(date)
        var monthDate = Timestamp(simpleDate)
        //monthDate = monthDate.getPreviousMonth(monthDate)


        println(monthDate)

        //habit.editDate(timestamp2, 2)

        // Use 17/03/2020 to add
        // Use 01/01/2021 to remove


        //var mList: MutableList<Habit> = mutableListOf()
        //mList.add(habit)
        //rw.write(mList)


    }
}