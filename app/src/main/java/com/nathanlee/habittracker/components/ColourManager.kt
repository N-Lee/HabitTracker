package com.nathanlee.habittracker.components

import android.content.Context
import androidx.core.content.ContextCompat

object ColourManager {
    val darkTheme: IntArray = intArrayOf(
        com.nathanlee.habittracker.R.color.dark_theme_grey,
        com.nathanlee.habittracker.R.color.dark_theme_blue,
        com.nathanlee.habittracker.R.color.dark_theme_green,
        com.nathanlee.habittracker.R.color.dark_theme_orange,
        com.nathanlee.habittracker.R.color.dark_theme_pink,
        com.nathanlee.habittracker.R.color.dark_theme_purple,
        com.nathanlee.habittracker.R.color.dark_theme_yellow
    )

    val partialCompleteDarkTheme: IntArray = intArrayOf(
        com.nathanlee.habittracker.R.color.dark_theme_light_grey,
        com.nathanlee.habittracker.R.color.dark_theme_light_blue,
        com.nathanlee.habittracker.R.color.dark_theme_light_green,
        com.nathanlee.habittracker.R.color.dark_theme_light_orange,
        com.nathanlee.habittracker.R.color.dark_theme_light_pink,
        com.nathanlee.habittracker.R.color.dark_theme_light_purple,
        com.nathanlee.habittracker.R.color.dark_theme_light_yellow
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
            com.nathanlee.habittracker.R.id.grey_radio -> {
                return "Grey"
            }
            com.nathanlee.habittracker.R.id.blue_radio -> {
                return "Blue"
            }

            com.nathanlee.habittracker.R.id.green_radio -> {
                return "Green"
            }

            com.nathanlee.habittracker.R.id.orange_radio -> {
                return "Orange"
            }

            com.nathanlee.habittracker.R.id.pink_radio -> {
                return "Pink"
            }

            com.nathanlee.habittracker.R.id.purple_radio -> {
                return "Purple"
            }

            com.nathanlee.habittracker.R.id.yellow_radio -> {
                return "Yellow"
            }
        }

        return "Grey"
    }

    fun colourStringToID(string: String): Int {
        when (string) {
            "Grey" -> {
                return com.nathanlee.habittracker.R.id.grey_radio
            }

            "Blue" -> {
                return com.nathanlee.habittracker.R.id.blue_radio
            }

            "Green" -> {
                return com.nathanlee.habittracker.R.id.green_radio
            }

            "Orange" -> {
                return com.nathanlee.habittracker.R.id.orange_radio
            }

            "Pink" -> {
                return com.nathanlee.habittracker.R.id.pink_radio
            }

            "Purple" -> {
                return com.nathanlee.habittracker.R.id.purple_radio
            }

            "Yellow" -> {
                return com.nathanlee.habittracker.R.id.yellow_radio
            }
        }

        return com.nathanlee.habittracker.R.id.grey_radio
    }
}