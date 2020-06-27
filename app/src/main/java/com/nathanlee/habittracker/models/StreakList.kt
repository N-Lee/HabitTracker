class StreakList {
    var streaks = mutableListOf<Streak>()
    var longest = 0

    /*
    Add an existing streak
     */
    fun add(streak: Streak) {
        streaks.add(streak)

        if (streak.length > longest){
            longest = streak.length
        }
    }

    /*
    Merge two streaks when the start/end of two streaks match
     */
    private fun mergeStreaks(first: Streak, second: Streak) {
        val firstIndex = this.find(0, streaks.size - 1, first.start)
        val secondIndex = this.find(0, streaks.size - 1, second.start)

        if (Timestamp(first.end.getNextDay()).compareTo(second.start) == 0) {
            streaks[firstIndex].end = second.end
            streaks.removeAt(secondIndex)

        } else if (Timestamp(second.end.getNextDay()).compareTo(first.end) == 0) {
            streaks[secondIndex].end = first.end
            streaks.removeAt(firstIndex)

        } else if (Timestamp(first.start.getPreviousDay()).compareTo(second.end) == 0) {
            streaks[secondIndex].end = first.end
            streaks.removeAt(firstIndex)

        } else if (Timestamp(second.start.getPreviousDay()).compareTo(first.end) == 0) {
            streaks[firstIndex].end = second.end
            streaks.removeAt(secondIndex)

        } else {
            //TODO: Decide if I want a message here
        }
    }

    /*
    Insert a Timestamp and edits an existing streak
    Or creates a new streak
     */
    fun editStreak(date: Timestamp, index: Int): Int {
        var streakIndex = index
        var editedStreakIndex: Int = -5

        if (index < 0) {
            streakIndex *= -1
        }

        when (streaks[streakIndex].isDateWithin(date)) {
            // Is within a streak
            0 -> {
                if (date.getDayOfYear() == streaks[streakIndex].start.getDayOfYear() && date.yearInt == streaks[streakIndex].start.yearInt) {
                    var nextDay = Timestamp(date.getNextDay())
                    streaks[streakIndex].start = nextDay
                } else if (date.getDayOfYear() == streaks[streakIndex].end.getDayOfYear() && date.yearInt == streaks[streakIndex].end.yearInt) {
                    var previousDay = Timestamp(date.getPreviousDay())
                    streaks[streakIndex].end = previousDay
                } else {
                    var newStreak = Streak(Timestamp(date.getNextDay()), streaks[streakIndex].end)
                    streaks[streakIndex].end = Timestamp(date.getPreviousDay())
                    streaks.add(streakIndex + 1, newStreak)
                }
                editedStreakIndex = streakIndex
            }
            // Is after a streak
            1 -> {
                var newStreak = Streak(date, date)
                streaks.add(streakIndex + 1, newStreak)
                editedStreakIndex = streakIndex + 1
            }
            // Is before a streak
            -1 -> {
                var newStreak = Streak(date, date)
                streaks.add(streakIndex, newStreak)
                editedStreakIndex = streakIndex - 1
            }
        }

        if (streakIndex != streaks.size - 1) {
            mergeStreaks(streaks[streakIndex], streaks[streakIndex + 1])
        }

        if (streakIndex != 0) {
            mergeStreaks(streaks[streakIndex - 1], streaks[streakIndex])
        }

        streaks[editedStreakIndex].length = streaks[editedStreakIndex].streakLength()
        longest = findLongestStreak()
        return editedStreakIndex
    }

    /*
    Go through all streaks and determine the longest
     */
    fun findLongestStreak(): Int {
        var longestStreak = 0

        for (i in streaks) {
            if (i.length > longestStreak) {
                longestStreak = i.length
            }
        }

        return longestStreak
    }

    /*
    Find a streak given a Timestamp. If not found, gives the closest index as a negative
     */
    fun find(l: Int, r: Int, x: Timestamp): Int {
        var mid: Int = l + (r - l) / 2

        if (r >= l) {
            if (streaks[mid].isDateWithin(x) == 0) {
                return mid
            }

            if (streaks[mid].isDateWithin(x) == -1) {
                if (mid == 0){
                    return 0
                }
                return find(l, mid - 1, x)
            }

            return find(mid + 1, r, x)
        }

        return -1 * (mid - 1)
    }

}