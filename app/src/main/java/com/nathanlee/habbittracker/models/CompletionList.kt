import java.util.*

class CompletionList() {
    var completions = mutableListOf<Completion>()

    // Create a weeks worth of Completions
    fun createWeek(date: Timestamp) {

        var incrementDate = date

        for (i in date.getDayOfWeek(date) until 7) {
            completions.add(Completion(incrementDate))
            incrementDate = Timestamp(incrementDate.getNextDay())
        }
    }

    // Given a new completion, fill in the dates between the new completion and the closest completion date
    fun edit(newCompletion: Completion, timePeriod: Int, status: Int) {
        var date = newCompletion.timestamp
        val first = completions.first()
        val last = completions.last()

        when (timePeriod) {
            -1 -> {
                var incrementDate = Timestamp(completions.first().timestamp.getPreviousDay())
                while (incrementDate.compareTo(date) != -1) {
                    completions.add(0, Completion(incrementDate, status))
                    incrementDate = Timestamp(incrementDate.getPreviousDay())
                }
            }
            1 -> {
                var incrementDate = Timestamp(completions.last().timestamp.getNextDay())

                while (incrementDate.compareTo(date) != 1) {
                    completions.add(Completion(incrementDate, status))
                    incrementDate = Timestamp(incrementDate.getNextDay())
                }
            }
            0 -> {
                var index = find(0, completions.size - 1, newCompletion.timestamp)
                completions[index].replace(newCompletion)
            }
        }
    }

    // Given a timestamp, find the completion
    fun find(l: Int, r: Int, x: Timestamp): Int {

        if (r >= l) {
            var mid: Int = l + (r - l) / 2
            if (completions[mid].timestamp.compareTo(x) == 0) {
                return mid
            }

            if (completions[mid].timestamp.compareTo(x) == 1) {
                return find(l, mid - 1, x)
            }

            return find(mid + 1, r, x)
        }

        return -1
    }

}