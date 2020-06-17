package com.nathanlee.habittracker.models

import Completion
import CompletionList
import Streak
import StreakList
import Timestamp
import kotlin.math.abs

class Habit(
    var name: String,
    var description: String,
    var colour: String,
    var numerator: Int,
    var denominator: Int,
    var id: Int,
    var notification: Boolean,
    var notificationTime: String,
    var notificationDays: BooleanArray
) {

    var completions: CompletionList = CompletionList()
    var streaks: StreakList = StreakList()

    /*
    Given a date, update the streak and completion status
     */
    fun editDate(date: Timestamp, completionStatus: Int) {
        val completion = Completion(date, completionStatus)
        val firstCompletion = completions.completions.first()
        val lastCompletion = completions.completions.last()

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

        if (startStreak == 0 && streaks.streaks[startStreak].start.compareTo(date) == 1) {
            startDate = date
        } else {
            if (startStreak < 0) {
                startStreak *= -1
                startDate = streaks.streaks[startStreak].start
            } else {
                startDate = streaks.streaks[startStreak].start
            }
        }

        index = completions.find(0, completions.completions.size, startDate)

        while (index < completions.completions.size) {
            if (isInStreak) {
                var startIndex = index
                if (isFrequencyMet(index)) {
                    index += denominator
                    markCompletions(startIndex, index - 1)
                } else {
                    var lastCompletedInPeriod = lastCompleteInPeriod(index)
                    if (lastCompletedInPeriod == -1) {
                        startIndex -= denominator
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
    private fun markCompletions(startIndex: Int, _endIndex: Int) {
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

                    if (count > numerator) {
                        completions.completions[i].status = 3
                    }
                }
                3 -> {
                    count++

                    if (count <= numerator) {
                        completions.completions[i].status = 2
                    }
                }
            }
        }
    }

    /*
    Given a period, check if frequency is met
     */
    private fun isFrequencyMet(startIndex: Int): Boolean {
        var counter = 0

        if (startIndex < 0) {
            System.err.println("Invalid period")
        }

        for (i in startIndex until startIndex + denominator) {
            if (i > completions.completions.size - 1) {
                if (counter >= numerator) {
                    return true
                }
                break
            }

            if (completions.completions[i].status == 2 || completions.completions[i].status == 3) {
                counter++
            }

            if (counter >= numerator) {
                return true
            }
        }

        return false
    }

    /*
    Finds the first date within a period that was completed
     */
    private fun firstCompleteInPeriod(startIndex: Int): Int {
        var endIndex = startIndex + denominator - 1

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
        var endIndex = startIndex + denominator - 1
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
        var streakIndex = abs(streaks.find(0, streaks.streaks.size - 1, date))
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

        index = if (date.compareTo(streaks.streaks[streakIndex].start) == -1) {
            0
        } else {
            completions.find(
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

            var periodEndIndex = index + denominator - 1

            if (periodEndIndex >= completions.completions.size - 1) {
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
                index += denominator
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
                        index = lastIndex + denominator
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

                    index += denominator
                }
            }

            if (index >= completions.completions.size) {
                index = completions.completions.size - 1
            }
        }
    }

    /*
    Replaces class variables
     */
    fun replace(
        name: String,
        description: String,
        colour: String,
        numerator: Int,
        denominator: Int,
        notification: Boolean,
        notificationTime: String,
        notificationDays: BooleanArray
    ) {
        this.name = name
        this.description = description
        this.colour = colour
        this.numerator = numerator
        this.denominator = denominator
        if (numerator == denominator) {
            this.numerator = 1
            this.denominator = 1
        }
        this.notification = notification
        this.notificationTime = notificationTime
        this.notificationDays = notificationDays
    }
}