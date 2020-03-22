import java.text.SimpleDateFormat
import java.time.*

data class Timestamp(val date: String) {
    var yearInt = date.substring(date.length - 4).toInt()
    var monthInt = date.substring(3, 5).toInt()
    var dayInt = date.substring(0, 2).toInt()

    /*
    See which day comes first between the two timestamps
     */
    fun compareTo(day: Timestamp): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val compareTimestamp = dateFormat.parse(day.date)
        if (dateFormat.parse(date).after(compareTimestamp)) {
            return 1 // this.date is after the compared day
        } else if (dateFormat.parse(date).before(compareTimestamp)) {
            return -1 // this.date is before the compared day
        }
        return 0
    }

    /*
    Get the day before this timestamp
     */
    fun getPreviousDay(): String {
        var previousYear: Int
        var previousMonth: Int
        var previousDay: Int
        var daysInMonth: IntArray = isLeapYear()

        if (dayInt > 1) {
            previousYear = yearInt
            previousMonth = monthInt
            previousDay = dayInt - 1

        } else if (dayInt == 1 && monthInt == 1) {
            previousYear = yearInt - 1
            previousMonth = 12
            previousDay = 31

        } else {
            previousYear = yearInt
            previousMonth = monthInt - 1
            previousDay = daysInMonth[previousMonth - 1]
        }

        val yearFormat = String.format("%04d", previousYear)
        val monthFormat = String.format("%02d", previousMonth)
        val dayFormat = String.format("%02d", previousDay)

        return dayFormat + "/" + monthFormat + "/" + yearFormat

    }

    /*
    Get day after this timestamp
     */
    fun getNextDay(): String {
        var tomorrowYear: Int
        var tomorrowMonth: Int
        var tomorrowDay: Int
        var daysInMonth: IntArray = isLeapYear()

        if (daysInMonth[monthInt - 1] > dayInt) {
            tomorrowYear = yearInt
            tomorrowMonth = monthInt
            tomorrowDay = dayInt + 1

        } else if (daysInMonth[monthInt - 1] == dayInt && monthInt == 12) {
            tomorrowYear = yearInt + 1
            tomorrowMonth = 1
            tomorrowDay = 1

        } else {
            tomorrowYear = yearInt
            tomorrowMonth = monthInt + 1
            tomorrowDay = 1
        }

        val yearFormat = String.format("%04d", tomorrowYear)
        val monthFormat = String.format("%02d", tomorrowMonth)
        val dayFormat = String.format("%02d", tomorrowDay)

        return dayFormat + "/" + monthFormat + "/" + yearFormat
    }

    /*
    Checks if this timestamp is during a leap year
     */
    fun isLeapYear(): IntArray {
        if (yearInt % 4 == 0 && (yearInt % 100 != 0 || yearInt % 400 == 100)) {
            return intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }

        return intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }

    /*
    Check if the given year is a leap year
     */
    fun isLeapYear(year: Int): Boolean {
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 100)) {
            return true
        }

        return false
    }

    /*
    Counts the number of days that have passed this year
     */
    fun getDayOfYear(): Int {
        var sum = 0
        var daysInMonth: IntArray = isLeapYear()

        for (i in 0 until monthInt - 1) {
            sum += daysInMonth[i]
        }

        sum += dayInt

        return sum
    }

    /*
    Get the day of the week (Monday, Tuesday, etc.)
     */
    fun getDayOfWeek(day: Timestamp): Int {
        var year = day.date.substring(day.date.length - 4).toInt()
        var month = day.date.substring(3, 5).toInt()
        var day = day.date.substring(0, 2).toInt()

        //TODO: decide whether I want to use API 15. Do I even need this?
        val localDate = LocalDate.of(year, month, day)
        return DayOfWeek.from(localDate).getValue()
    }

    /*
    Add "number" amount of days and returns the date
     */
    fun getDaysAfter(number: Int): Timestamp {
        var year = yearInt
        var month = monthInt
        var day = dayInt + number
        var daysInMonth: IntArray = isLeapYear()

        if (isLeapYear(year)) {
            daysInMonth = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        } else {
            daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        }

        while (day > daysInMonth[month - 1]) {

            day -= daysInMonth[month - 1]
            month++

            if (month > 12) {

                month = 1
                ++year

                if (isLeapYear(year)) {
                    daysInMonth = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
                } else {
                    daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
                }

            }


        }

        val yearFormat = String.format("%04d", year)
        val monthFormat = String.format("%02d", month)
        val dayFormat = String.format("%02d", day)

        return Timestamp(dayFormat + "/" + monthFormat + "/" + yearFormat)
    }

    /*
    Subtract "number" amount of days and returns the date
     */
    fun getDaysBefore(number: Int): Timestamp {
        var year = yearInt
        var month = monthInt
        var day = dayInt - number
        var daysInMonth: IntArray = isLeapYear()

        while (day < 1) {

            --month
            day += daysInMonth[month - 1]

            if (month < 1) {

                month = 12
                --year

                if (isLeapYear(year)) {
                    daysInMonth = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
                } else {
                    daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
                }

            }


        }

        val yearFormat = String.format("%04d", year)
        val monthFormat = String.format("%02d", month)
        val dayFormat = String.format("%02d", day)

        return Timestamp(dayFormat + "/" + monthFormat + "/" + yearFormat)
    }

    /*
    Given two dates, determines if this timestamp falls between the two given dates (inclusive)
     */
    fun isWithin(start: Timestamp, end: Timestamp): Boolean{
        if (start.compareTo(this) != 1 && end.compareTo(this) != -1) { return true }
        return false
    }

    /*
    Returns the date as a string
     */
    override fun toString(): String {
        return date
    }
}