class Streak(var start: Timestamp, var end: Timestamp) {
    var length: Int = streakLength()

    /*
    How many days is the current streak
     */
    fun streakLength(): Int {
        var length = 0

        if (start.yearInt != end.yearInt) {
            for (i in start.yearInt + 1 until end.yearInt) {
                if (start.isLeapYear(i)) {
                    length += 366
                } else {
                    length += 365
                }
            }
            if (start.isLeapYear(start.yearInt)) {
                length += 366 - start.getDayOfYear() + 1
            } else {
                length += 365 - start.getDayOfYear() + 1
            }

            if (end.isLeapYear(end.yearInt) && end.monthInt > 2) {
                length += end.getDayOfYear() + 1
            } else {
                length += end.getDayOfYear()
            }


        } else {
            length += end.getDayOfYear() - start.getDayOfYear() + 1
        }

        return length
    }

    // Is the current streak longer than the given streak
    fun isLonger(previous: Streak): Boolean {
        if (previous.length > this.length) {
            return false
        }

        return true
    }

    /*
    Is the timestamp within the streak
     */
    fun isDateWithin(date: Timestamp): Int {
        val dateOfYear = date.getDayOfYear()
        val dateYear = date.yearInt

        if (dateYear == start.yearInt &&
            start.yearInt == end.yearInt &&
            dateOfYear >= start.getDayOfYear() &&
            dateOfYear <= end.getDayOfYear()
        ) {
            return 0
        } else if (dateYear == start.yearInt &&
            dateYear != end.yearInt &&
            dateOfYear >= start.getDayOfYear()
        ) {
            return 0
        } else if (dateYear == end.yearInt &&
            dateYear != start.yearInt &&
            dateOfYear <= end.getDayOfYear()
        ) {
            return 0
        } else if (dateYear > start.yearInt && dateYear < end.yearInt) {
            return 0
        }

        if (date.compareTo(end) == 1) {
            return 1
        }

        return -1
    }

    /*
    Returns start and end date of streak
     */
    override fun toString(): String {
        return "$start - $end"
    }
}