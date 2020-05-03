package com.nathanlee.habittracker.components

import android.content.Context
import androidx.core.content.ContextCompat

object ColourManager {
    val DARK_THEME: IntArray = intArrayOf(
        com.nathanlee.habittracker.R.color.darkThemeGrey,
        com.nathanlee.habittracker.R.color.darkThemeBlue,
        com.nathanlee.habittracker.R.color.darkThemeGreen,
        com.nathanlee.habittracker.R.color.darkThemeOrange,
        com.nathanlee.habittracker.R.color.darkThemePink,
        com.nathanlee.habittracker.R.color.darkThemePurple,
        com.nathanlee.habittracker.R.color.darkThemeYellow
    )

    val PARTIAL_COMPLETE_DARK_THEME: IntArray = intArrayOf(
        com.nathanlee.habittracker.R.color.darkThemeLightGrey,
        com.nathanlee.habittracker.R.color.darkThemeLightBlue,
        com.nathanlee.habittracker.R.color.darkThemeLightGreen,
        com.nathanlee.habittracker.R.color.darkThemeLightOrange,
        com.nathanlee.habittracker.R.color.darkThemeLightPink,
        com.nathanlee.habittracker.R.color.darkThemeLightPurple,
        com.nathanlee.habittracker.R.color.darkThemeLightYellow
    )

    fun selectColour(colour: String, context: Context): Int {
        when (colour) {
            "Grey" -> {
                return ContextCompat.getColor(context, DARK_THEME[0])
            }
            "Blue" -> {
                return ContextCompat.getColor(context, DARK_THEME[1])
            }

            "Green" -> {
                return ContextCompat.getColor(context, DARK_THEME[2])
            }

            "Orange" -> {
                return ContextCompat.getColor(context, DARK_THEME[3])
            }

            "Pink" -> {
                return ContextCompat.getColor(context, DARK_THEME[4])
            }

            "Purple" -> {
                return ContextCompat.getColor(context, DARK_THEME[5])
            }

            "Yellow" -> {
                return ContextCompat.getColor(context, DARK_THEME[6])
            }
        }

        return ContextCompat.getColor(context, DARK_THEME[0])
    }

    fun selectPartialCompleteColour(colour: String, context: Context): Int {
        when (colour) {
            "Grey" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[0])
            }
            "Blue" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[1])
            }

            "Green" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[2])
            }

            "Orange" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[3])
            }

            "Pink" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[4])
            }

            "Purple" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[5])
            }

            "Yellow" -> {
                return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[6])
            }
        }

        return ContextCompat.getColor(context, PARTIAL_COMPLETE_DARK_THEME[0])
    }

    fun colourIdToString(colourId: Int, context: Context): String {
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
}