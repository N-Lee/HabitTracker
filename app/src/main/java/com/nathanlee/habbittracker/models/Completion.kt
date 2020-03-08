data class Completion(var timestamp: Timestamp) {
    var status: Int = 0
    lateinit var colour: String

    constructor(_timestamp: Timestamp, _status: Int) : this(_timestamp) {
        status = _status
    }

    // 0:  Didn't do it
    // 1:  Didn't do it on day of, but passed due to frequency
    // 2:  Did it
    // 3:  Did it more times than frequency states

    fun replace(other: Completion) {
        status = other.status
        timestamp = other.timestamp
        colour = other.colour
    }
}