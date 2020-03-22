class Frequency (var numerator: Int, var denominator: Int){

    /*
    Returns the number of times the user wants to do the habit in a length of time
     */
    override fun toString(): String{
        return "Will do " + numerator + " out of " + denominator + " days"
    }

}