data class Completion(var timestamp: Timestamp) {
    var status: Int = 0

    constructor(_timestamp: Timestamp, _status: Int) : this(_timestamp) {
        status = _status
    }

    /*
    Status:
    0:  Incomplete (Didn't do it)
    1:  Partial complete (Didn't do it on day of, but passed due to frequency)
    2:  Complete (Did it)
    3:  Extra complete (Did it more times than frequency)
     */

    /*
    Replaces the values of this completion with the given completion
     */
    fun replace(other: Completion) {
        status = other.status
        timestamp = other.timestamp
    }
}