package components

import android.content.Context
import androidx.core.content.ContextCompat
import com.nathanlee.habittracker.R

object ColourManager {
    val darkTheme: IntArray = intArrayOf(
        R.color.dark_theme_grey,
        R.color.dark_theme_blue,
        R.color.dark_theme_green,
        R.color.dark_theme_orange,
        R.color.dark_theme_pink,
        R.color.dark_theme_purple,
        R.color.dark_theme_yellow
    )

    val partialCompleteDarkTheme: IntArray = intArrayOf(
        R.color.dark_theme_light_grey,
        R.color.dark_theme_light_blue,
        R.color.dark_theme_light_green,
        R.color.dark_theme_light_orange,
        R.color.dark_theme_light_pink,
        R.color.dark_theme_light_purple,
        R.color.dark_theme_light_yellow
    )

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