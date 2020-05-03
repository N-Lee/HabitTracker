package com.nathanlee.habittracker.models

import Completion
import CompletionList
import Frequency
import Streak
import StreakList
import Timestamp
import android.os.Parcel
import android.os.Parcelable

class Habit(
    var name: String,
    var description: String,
    var colour: String,
    var numerator: Int,
    var denominator: Int
) : Parcelable {

    private var frequency: Frequency = Frequency(numerator, denominator)
    var completions: CompletionList = CompletionList()
    var streaks: StreakList = StreakList()

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    /*
    Given a date, update the streak and completion status
     */
    fun editDate(date: Timestamp, completionStatus: Int) {
        val completion = Completion(date, completionStatus)
        val firstCompletion = completions.completions.first()
        val lastCompletion = completions.completions.last()

        // If before first date

        when {
            firstCompletion.timestamp.compareTo(date) == 1 -> {
                completions.edit(completion, -1)

            }
            lastCompletion.timestamp.compareTo(date) == -1 -> {
                completions.edit(completion, 1)

            }
            else -> {
                completions.edit(completion, 0)

            }
        }

        updatePeriodCompletions(date)
        updatePeriodStreak(date)
    }

    /*
    Every date will fall under a period that is the length of the frequency denominator
    Determines what the start and end date is of the period the date falls under
     */
    fun getPeriod(date: Timestamp): Array<Timestamp> {
        if (streaks.streaks.isEmpty()) {
            System.err.println("Streaks is empty when trying to find period")
        }

        var timestampStreakIndex = streaks.find(0, streaks.streaks.size - 1, date)
        var currentStartPeriod = completions.completions[0].timestamp
        lateinit var currentEndPeriod: Timestamp
        lateinit var currentPeriod: Array<Timestamp>

        if (date.compareTo(currentStartPeriod) == -1 || timestampStreakIndex < 0) {
            currentEndPeriod = date.getDaysAfter(frequency.denominator - 1)
            return arrayOf(date, currentEndPeriod)
        } else if (timestampStreakIndex >= 0) {
            currentStartPeriod = streaks.streaks[timestampStreakIndex].start
            currentEndPeriod = currentStartPeriod.getDaysAfter(frequency.denominator - 1)
            currentPeriod = arrayOf(currentStartPeriod, currentEndPeriod)

            while (currentEndPeriod.compareTo(date) == -1) {
                currentStartPeriod = currentStartPeriod.getDaysAfter(frequency.denominator)
                currentEndPeriod = currentStartPeriod.getDaysAfter(frequency.denominator - 1)
                currentPeriod = arrayOf(currentStartPeriod, currentEndPeriod)
            }
        }
        //println("Start: " + currentPeriod[0].toString() + " End: " + currentPeriod[1].toString())
        return currentPeriod
    }

    /*
    Goes through every completions starting from the streak start of the given date
    Then it marks whether a status should remain incomplete or should pass due to the frequency being met
    */
    fun updatePeriodCompletions(date: Timestamp) {
        var isInStreak = false
        var startStreak = streaks.find(0, streaks.streaks.size - 1, date)
        var index: Int
        var startDate: Timestamp

        if (streaks.streaks.isEmpty()) {
            return
        }

        if (startStreak < 0) {
            startStreak *= -1
            startDate = streaks.streaks[startStreak].start
        } else {
            startDate = streaks.streaks[startStreak].start
        }

        index = completions.find(0, completions.completions.size, startDate)

        while (index < completions.completions.size) {
            if (isInStreak) {
                var startIndex = index
                if (isFrequencyMet(index)) {
                    index += frequency.denominator
                    markCompletions(startIndex, index - 1)
                } else {
                    var lastCompletedInPeriod = lastCompleteInPeriod(index)
                    if (lastCompletedInPeriod == -1) {
                        startIndex -= frequency.denominator
                        --index
                    } else {
                        index = lastCompletedInPeriod
                    }
                    isInStreak = false
                    markCompletions(startIndex, index)
                    ++index
                }
            } else {
                when (completions.completions[index].status) {
                    0, 1 -> {
                        completions.completions[index].status = 0
                        index++
                    }

                    2 -> {
                        isInStreak = true
                    }

                    3 -> {
                        completions.completions[index].status = 2
                        isInStreak = true
                    }
                }
            }
        }
    }

    /*
    Given a time frame, marks all incomplete as partial complete
    Marks completes as extra complete if frequency is met
    Marks extra completes as complete if frequency is not met
     */
    fun markCompletions(startIndex: Int, _endIndex: Int) {
        var endIndex = _endIndex
        var count = 0

        if (endIndex >= completions.completions.size) {
            endIndex = completions.completions.size - 1
        }

        for (i in startIndex..endIndex) {
            when (completions.completions[i].status) {
                0 -> {
                    completions.completions[i].status = 1
                }
                2 -> {
                    count++

                    if (count > frequency.numerator) {
                        completions.completions[i].status = 3
                    }
                }
                3 -> {
                    count++

                    if (count <= frequency.numerator) {
                        completions.completions[i].status = 2
                    }
                }
            }
        }
    }

    /*
    Given a period, check if frequency is met
     */
    fun isFrequencyMet(startIndex: Int): Boolean {
        var counter = 0

        if (startIndex < 0) {
            System.err.println("Invalid period")
        }

        for (i in startIndex until startIndex + frequency.denominator) {
            if (i > completions.completions.size - 1) {
                if (counter >= frequency.numerator) {
                    return true
                }
                break
            }

            if (completions.completions[i].status == 2 || completions.completions[i].status == 3) {
                counter++
            }

            if (counter >= frequency.numerator) {
                return true
            }
        }

        return false
    }

    /*
    Finds the first date within a period that was completed
     */
    fun firstCompleteInPeriod(startIndex: Int): Int {
        var endIndex = startIndex + frequency.denominator - 1

        if (startIndex == -1) {
            return -1
        }

        if (endIndex > completions.completions.size - 1) {
            endIndex = completions.completions.size - 1
        }

        for (i in startIndex..endIndex) {
            if (completions.completions[i].status == 2) {
                return i
            }
        }

        return -1

    }

    /*
    Finds the last date within a period that was completed
     */
    private fun lastCompleteInPeriod(startIndex: Int): Int {
        var endIndex = startIndex + frequency.denominator - 1
        var lastIndex = -1

        if (startIndex == -1) {
            return -1
        }

        if (endIndex > completions.completions.size - 1) {
            endIndex = completions.completions.size - 1
        }

        for (i in startIndex..endIndex) {
            if (completions.completions[i].status == 2) {
                lastIndex = i
            }
        }

        return lastIndex
    }

    /*
    Empties streak list, goes through every date in completions starting at given date, and adds all streaks
     */
    fun updatePeriodStreak(date: Timestamp) {
        var streakIndex = Math.abs(streaks.find(0, streaks.streaks.size - 1, date))
        var index: Int
        var isStartStreak = true
        var isLastPeriod = false
        lateinit var startTimestamp: Timestamp
        lateinit var endTimestamp: Timestamp
        lateinit var newStreak: Streak

        if (streaks.streaks.size == 0) {
            streakIndex = 0
            newStreak = Streak(date, date)
            streaks.add(newStreak)
        }

        if (date.compareTo(streaks.streaks[streakIndex].start) == -1) {
            index = 0
        } else {
            index = completions.find(
                0,
                completions.completions.size - 1,
                streaks.streaks[streakIndex].start
            )
        }

        streaks.streaks.subList(streakIndex, streaks.streaks.size).clear()
        streaks.longest = 0

        while (!isLastPeriod) {
            while (isStartStreak && (completions.completions[index].status != 2 && completions.completions[index].status != 3)) {
                index++
                if (index > completions.completions.size - 1) {
                    return
                }
            }

            var periodEndIndex = index + frequency.denominator - 1

            if (periodEndIndex > completions.completions.size - 1) {
                isLastPeriod = true
            }

            var firstIndex = firstCompleteInPeriod(index)
            var lastIndex = lastCompleteInPeriod(index)

            if (isFrequencyMet(index)) {
                if (isStartStreak) {
                    startTimestamp = completions.completions[firstIndex].timestamp
                    isStartStreak = false
                }
                if (isLastPeriod) {
                    endTimestamp = completions.completions.last().timestamp
                    newStreak = Streak(startTimestamp, endTimestamp)
                    streaks.add(newStreak)
                }
                index += frequency.denominator
            } else {
                if (firstIndex != -1) {
                    if (isStartStreak) {
                        startTimestamp = completions.completions[firstIndex].timestamp
                    }
                    endTimestamp = completions.completions[lastIndex].timestamp
                    newStreak = Streak(startTimestamp, endTimestamp)
                    streaks.add(newStreak)
                    isStartStreak = true
                    var firstCompleteNextPeriod = firstCompleteInPeriod(lastIndex + 1)
                    if (firstCompleteNextPeriod == -1) {
                        index = lastIndex + frequency.denominator
                    } else {
                        index = firstCompleteNextPeriod
                    }
                } else {
                    if (!isStartStreak) {
                        endTimestamp = completions.completions[index - 1].timestamp
                        newStreak = Streak(startTimestamp, endTimestamp)
                        streaks.add(newStreak)
                        isStartStreak = true
                    }

                    index += frequency.denominator
                }
            }

            if (index >= completions.completions.size) {
                index = completions.completions.size - 1
            }
        }
    }

    fun getCompletionsInMonth(date: Timestamp): MutableList<Completion> {
        val index = completions.find(0, completions.completions.size, date)
        val today: Timestamp = completions.completions[index].timestamp
        val monthLength = today.monthLength(today)
        val dayInt = today.dayInt
        val daysSinceFirstDayInMonth = dayInt - 1
        val daysUntilLastDayInMonth = monthLength - dayInt
        val firstDayIndex = index - daysSinceFirstDayInMonth
        val lastDayIndex = index + daysUntilLastDayInMonth
        var completionsInMonth = mutableListOf<Completion>()

        for (i in firstDayIndex..lastDayIndex) {
            if (i < completions.completions.size) {
                completionsInMonth.add(completions.completions[i])
            } else {
                return completionsInMonth
            }
        }

        return completionsInMonth

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(colour)
        parcel.writeInt(numerator)
        parcel.writeInt(denominator)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Habit> {
        override fun createFromParcel(parcel: Parcel): Habit {
            return Habit(parcel)
        }

        override fun newArray(size: Int): Array<Habit?> {
            return arrayOfNulls(size)
        }
    }
}