package com.nathanlee.habittracker.models

import org.junit.Test
import Timestamp
import ReadWriteJson

class HabitTest {

    @Test
    fun updateAllCompletions() {

        val rw = ReadWriteJson()
        val test = rw.read()
        var habit = test[0]
        var completions = habit.completions.completions
        var streaks = habit.streaks.streaks
        var index = 0

        var timestamp1 = Timestamp("17/03/2020") // Change to 2
        var timestamp2 = Timestamp("01/01/2021") // Change to 0
        var timestamp3 = Timestamp("10/02/2020") // Change to whatever
        var timestamp4 = Timestamp("01/01/2022") // Change to whatever

        habit.editDate(timestamp2, 0)
        //habit.editDate(timestamp2, 2)

        // Use 17/03/2020 to add
        // Use 01/01/2021 to remove


        var mList: MutableList<Habit> = mutableListOf()
        mList.add(habit)
        rw.write(mList)

    }
}