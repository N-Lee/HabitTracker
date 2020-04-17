package com.nathanlee.habittracker.models

import android.content.Context
import androidx.core.content.ContextCompat

object ColourManager{
    val DARK_THEME: IntArray = intArrayOf(
        com.nathanlee.habittracker.R.color.darkThemeGrey,
        com.nathanlee.habittracker.R.color.darkThemeBlue,
        com.nathanlee.habittracker.R.color.darkThemeGreen,
        com.nathanlee.habittracker.R.color.darkThemeOrange,
        com.nathanlee.habittracker.R.color.darkThemePink,
        com.nathanlee.habittracker.R.color.darkThemePurple,
        com.nathanlee.habittracker.R.color.darkThemeYellow
    )

    fun selectColour(colourId: Int, context: Context): Int{
        when (colourId) {
            com.nathanlee.habittracker.R.id.greyRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[0])
            }
            com.nathanlee.habittracker.R.id.blueRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[1])
            }

            com.nathanlee.habittracker.R.id.greenRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[2])
            }

            com.nathanlee.habittracker.R.id.orangeRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[3])
            }

            com.nathanlee.habittracker.R.id.pinkRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[4])
            }

            com.nathanlee.habittracker.R.id.purpleRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[5])
            }

            com.nathanlee.habittracker.R.id.yellowRadio -> {
                return ContextCompat.getColor(context, DARK_THEME[6])
            }
        }

        return ContextCompat.getColor(context, DARK_THEME[0])
    }
}