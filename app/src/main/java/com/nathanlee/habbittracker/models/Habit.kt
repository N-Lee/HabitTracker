class Habit(
    var name: String,
    var description: String,
    var colour: String,
    var weekendsOff: Boolean,
    var numerator: Int,
    var denominator: Int
) {
    var frequency: Frequency = Frequency(numerator, denominator)

    var completions: CompletionList = CompletionList()
    var streaks: StreakList = StreakList()

    fun addDate(date: Timestamp, completionStatus: Int) {

        val completion = Completion(date, completionStatus)
        val firstTimestamp = completions.completions.first()
        val firstDayofWeek = date.getDayOfWeek(firstTimestamp.timestamp)
        val lastTimestamp = completions.completions.last()

        var streakIndex: Int = streaks.find(0, streaks.streaks.size - 1, date)
        var period = getFrequencyPeriodDate(date)
        var previousPeriod = getPreviousPeriod(period)
        var completionsInPrevious = mutableListOf<Completion>()
        var index = completions.find(0, completions.completions.size - 1, previousPeriod[0])

        // If before first date
        if (firstTimestamp.timestamp.compareTo(date) == 1) {



            for (i in 0 until frequency.denominator){
                completionsInPrevious.add(completions.completions[index + i])
            }

            // Determine if within frequency


            // Update completion and streak
            completions.edit(completion, -1, 0) // TODO: status is placeholder

            // If After last date
        } else if (lastTimestamp.timestamp.compareTo(date) == -1) {

            completions.edit(completion, 1, 0) // TODO: status is placeholder

        } else {

            completions.edit(completion, 0, 0) // TODO: status is placeholder

        }


        // Edit Streak


    }

    fun getNewFrequencyPeriod(date: Timestamp): Array<Timestamp>{
        val endPeriod = date.getDaysAfter(frequency.denominator - 1)
        return arrayOf(date, endPeriod)
    }

    // Every date will fall under a period that is the length of the frequency denominator
    // Determines what the start and end date is of the period the date falls under
    fun getFrequencyPeriodDate(date: Timestamp): Array<Timestamp> {
        var currentStartPeriod = completions.completions[0].timestamp
        var currentEndPeriod = currentStartPeriod.getDaysAfter(frequency.denominator - 1)

        while (currentEndPeriod.compareTo(date) == -1) {
            currentStartPeriod = currentStartPeriod.getDaysAfter(frequency.denominator)
            currentEndPeriod = currentStartPeriod.getDaysAfter(frequency.denominator - 1)
        }

        return arrayOf(currentStartPeriod, currentEndPeriod)
    }

    // Given a period, find the period immediately before this one
    fun getPreviousPeriod(period: Array<Timestamp>): Array<Timestamp>{
        return arrayOf(period[0].getDaysBefore(frequency.denominator), period[1].getDaysBefore(frequency.denominator))
    }

    fun updateAllCompletions(){
        val timePerPeriod = frequency.numerator
        val periodLength = frequency.denominator

        var passedIndex = mutableListOf<Int>()
        var completePerPeriod = 0

        for (index in 0 until completions.completions.size){
            if (completePerPeriod >= timePerPeriod){
                for (i in passedIndex){
                    completions.completions[i].status = 1
                }
                passedIndex.clear()
                if (completions.completions[index].status == 0){
                    completions.completions[index].status = 1
                }
            }

            if (completions.completions[index].status == 2 || completions.completions[index].status == 3){
                ++completePerPeriod
            } else {
                passedIndex.add(index)
            }

            if ((index + 1) % periodLength == 0){
                passedIndex.clear()
                completePerPeriod = 0
            }
        }

    }

    fun updatePeriodFrequency(timePeriod: Int, date: Timestamp){
        /*
            Find the first day of any streak
            Find the period
            Cycle through the periods until it reaches the desired date
            Check how many days are needed to be done within period
            Check how many days within the period are already done
            Make the last days within the period that's past the number needed to be done to be the extra days
        */
        when (timePeriod){
            -1 -> {
                val dateFrequency = getNewFrequencyPeriod(date)
                // Take Index because the completions will need to be updated
                var completionsInPeriodIndex = mutableListOf<Int>()

                var i = 0
                while (completions.completions[i].timestamp.compareTo(dateFrequency[1]) != 1){
                    if (completions.completions[i].status == 2 || completions.completions[i].status == 3){
                        completionsInPeriodIndex.add(i)
                    }
                    i++
                }
            }
            1 -> {

            }
            0 -> {

            }
        }
    }
}