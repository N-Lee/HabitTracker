package components

import android.content.Context
import androidx.core.content.ContextCompat
import com.nathanlee.habittracker.R

object ColourManager {
    private val darkTheme: IntArray = intArrayOf(
        R.color.dark_theme_grey,
        R.color.dark_theme_blue,
        R.color.dark_theme_green,
        R.color.dark_theme_orange,
        R.color.dark_theme_pink,
        R.color.dark_theme_purple,
        R.color.dark_theme_yellow
    )

    private val veryDarkDarkTheme: IntArray = intArrayOf(
        R.color.dark_theme_very_dark_grey,
        R.color.dark_theme_very_dark_blue,
        R.color.dark_theme_very_dark_green,
        R.color.dark_theme_very_dark_orange,
        R.color.dark_theme_very_dark_pink,
        R.color.dark_theme_very_dark_purple,
        R.color.dark_theme_very_dark_yellow
    )

    private val darkDarkTheme: IntArray = intArrayOf(
        R.color.dark_theme_dark_grey,
        R.color.dark_theme_dark_blue,
        R.color.dark_theme_dark_green,
        R.color.dark_theme_dark_orange,
        R.color.dark_theme_dark_pink,
        R.color.dark_theme_dark_purple,
        R.color.dark_theme_dark_yellow
    )

    private val lightDarkTheme: IntArray = intArrayOf(
        R.color.dark_theme_light_grey,
        R.color.dark_theme_light_blue,
        R.color.dark_theme_light_green,
        R.color.dark_theme_light_orange,
        R.color.dark_theme_light_pink,
        R.color.dark_theme_light_purple,
        R.color.dark_theme_light_yellow
    )

    private val partialCompleteDarkTheme: IntArray = intArrayOf(
        R.color.dark_theme_transparent_grey,
        R.color.dark_theme_transparent_blue,
        R.color.dark_theme_transparent_green,
        R.color.dark_theme_transparent_orange,
        R.color.dark_theme_transparent_pink,
        R.color.dark_theme_transparent_purple,
        R.color.dark_theme_transparent_yellow
    )

    /*
    Using the colour in habit objects, get the associated colour in hex
     */
    fun selectColour(colour: String, context: Context): Int {
        when (colour) {
            "Grey" -> {
                return ContextCompat.getColor(context, darkTheme[0])
            }
            "Blue" -> {
                return ContextCompat.getColor(context, darkTheme[1])
            }

            "Green" -> {
                return ContextCompat.getColor(context, darkTheme[2])
            }

            "Orange" -> {
                return ContextCompat.getColor(context, darkTheme[3])
            }

            "Pink" -> {
                return ContextCompat.getColor(context, darkTheme[4])
            }

            "Purple" -> {
                return ContextCompat.getColor(context, darkTheme[5])
            }

            "Yellow" -> {
                return ContextCompat.getColor(context, darkTheme[6])
            }
        }

        return ContextCompat.getColor(context, darkTheme[0])
    }

    /*
    Using the colour in habit objects, get the associated colour in hex. Used for the calendar view when a completion is partial complete
     */
    fun selectPartialCompleteColour(colour: String, context: Context): Int {
        when (colour) {
            "Grey" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[0])
            }
            "Blue" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[1])
            }

            "Green" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[2])
            }

            "Orange" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[3])
            }

            "Pink" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[4])
            }

            "Purple" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[5])
            }

            "Yellow" -> {
                return ContextCompat.getColor(context, partialCompleteDarkTheme[6])
            }
        }

        return ContextCompat.getColor(context, partialCompleteDarkTheme[0])
    }

    /*
    Using the colour in habit objects, get the associated colour in hex. Used for pie chart colours
     */
    fun selectGroupColour(colour: String, context: Context): MutableList<String> {
        var groupColour = mutableListOf<String>()

        when (colour) {
            "Grey" -> {
                groupColour.add(context.getString(lightDarkTheme[0]))
                groupColour.add(context.getString(darkTheme[0]))
                groupColour.add(context.getString(darkDarkTheme[0]))
                groupColour.add(context.getString(veryDarkDarkTheme[0]))
            }
            "Blue" -> {
                groupColour.add(context.getString(lightDarkTheme[1]))
                groupColour.add(context.getString(darkTheme[1]))
                groupColour.add(context.getString(darkDarkTheme[1]))
                groupColour.add(context.getString(veryDarkDarkTheme[1]))
            }

            "Green" -> {
                groupColour.add(context.getString(lightDarkTheme[2]))
                groupColour.add(context.getString(darkTheme[2]))
                groupColour.add(context.getString(darkDarkTheme[2]))
                groupColour.add(context.getString(veryDarkDarkTheme[2]))
            }

            "Orange" -> {
                groupColour.add(context.getString(lightDarkTheme[3]))
                groupColour.add(context.getString(darkTheme[3]))
                groupColour.add(context.getString(darkDarkTheme[3]))
                groupColour.add(context.getString(veryDarkDarkTheme[3]))

            }

            "Pink" -> {
                groupColour.add(context.getString(lightDarkTheme[4]))
                groupColour.add(context.getString(darkTheme[4]))
                groupColour.add(context.getString(darkDarkTheme[4]))
                groupColour.add(context.getString(veryDarkDarkTheme[4]))
            }

            "Purple" -> {
                groupColour.add(context.getString(lightDarkTheme[5]))
                groupColour.add(context.getString(darkTheme[5]))
                groupColour.add(context.getString(darkDarkTheme[5]))
                groupColour.add(context.getString(veryDarkDarkTheme[5]))
            }

            "Yellow" -> {
                groupColour.add(context.getString(lightDarkTheme[6]))
                groupColour.add(context.getString(darkTheme[6]))
                groupColour.add(context.getString(darkDarkTheme[6]))
                groupColour.add(context.getString(veryDarkDarkTheme[6]))
            }
        }

        return groupColour
    }

    fun colourIdToString(colourId: Int): String {
        when (colourId) {
            R.id.grey_radio -> {
                return "Grey"
            }
            R.id.blue_radio -> {
                return "Blue"
            }

            R.id.green_radio -> {
                return "Green"
            }

            R.id.orange_radio -> {
                return "Orange"
            }

            R.id.pink_radio -> {
                return "Pink"
            }

            R.id.purple_radio -> {
                return "Purple"
            }

            R.id.yellow_radio -> {
                return "Yellow"
            }
        }

        return "Grey"
    }

    fun colourStringToID(string: String): Int {
        when (string) {
            "Grey" -> {
                return R.id.grey_radio
            }

            "Blue" -> {
                return R.id.blue_radio
            }

            "Green" -> {
                return R.id.green_radio
            }

            "Orange" -> {
                return R.id.orange_radio
            }

            "Pink" -> {
                return R.id.pink_radio
            }

            "Purple" -> {
                return R.id.purple_radio
            }

            "Yellow" -> {
                return R.id.yellow_radio
            }
        }

        return R.id.grey_radio
    }
}