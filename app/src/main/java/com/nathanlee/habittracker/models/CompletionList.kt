class CompletionList {
    var completions = mutableListOf<Completion>()

    /*
    Given a new completion, fill in the dates between the new completion and the closest completion date
    Given an existing date, change it's values
     */
    fun edit(newCompletion: Completion, timePeriod: Int) {
        var date = newCompletion.timestamp

        if (completions.isEmpty()){
            completions.add(newCompletion)
            return
        }

        when (timePeriod) {
            -1 -> {
                var incrementDate = Timestamp(completions.first().timestamp.getPreviousDay())

                while (incrementDate.compareTo(date) == 1) {
                    completions.add(0, Completion(incrementDate, 0))
                    incrementDate = Timestamp(incrementDate.getPreviousDay())
                }

                completions.add(0, newCompletion)
            }
            1 -> {
                var incrementDate = Timestamp(completions.last().timestamp.getNextDay())

                while (incrementDate.compareTo(date) == -1) {
                    completions.add(Completion(incrementDate, 0))
                    incrementDate = Timestamp(incrementDate.getNextDay())
                }

                completions.add(newCompletion)
            }
            0 -> {
                var index = find(0, completions.size - 1, newCompletion.timestamp)

                completions[index].replace(newCompletion)
            }
        }
    }

    /*
    Given a timestamp, find the completion
     */
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