package com.nathanlee.habittracker.components

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nathanlee.habittracker.R
import components.ColourManager

class StreakChartView(var habitIndex: Int, var streakChart: BarChart, var context: Context) {

    private val sizePerBar = 100
    private val baseSize = 140
    private var colour = (ColourManager.selectColour(
        HabitManager.habitList[habitIndex].colour,
        context
    ))

    fun createGraph(layout: LinearLayout) {
        var habitEntries = mutableListOf<BarEntry>()
        var streakDates = mutableListOf<String>()
        var maxEntry: Int = 0

        for (i in 0 until HabitManager.habitList[habitIndex].streaks.streaks.size) {
            val wantedStreak = HabitManager.habitList[habitIndex].streaks.streaks[i]
            habitEntries.add(BarEntry(i.toFloat(), wantedStreak.length.toFloat()))
            streakDates.add(wantedStreak.toString())
            if (wantedStreak.length > maxEntry) {
                maxEntry = wantedStreak.length
            }
        }

        var dataSet = BarDataSet(habitEntries, "")
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor =
            ContextCompat.getColor(context, R.color.dark_theme_title)
        dataSet.color = colour
        dataSet.valueFormatter = (object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0f", kotlin.math.floor(value))
            }
        })


        var barData = BarData(dataSet)
        streakChart.data = barData
        streakChart.setPinchZoom(false)
        streakChart.legend.isEnabled = false
        streakChart.description.isEnabled = false
        streakChart.axisLeft.isEnabled = false
        streakChart.setDrawValueAboveBar(false)
        streakChart.setDrawGridBackground(false)
        streakChart.setTouchEnabled(false)
        streakChart.isDragEnabled = false
        streakChart.isDoubleTapToZoomEnabled = false

        val axisLeft = streakChart.axisLeft
        axisLeft.setDrawGridLines(false)
        axisLeft.axisMinimum = 0f

        val axisRight = streakChart.axisRight
        axisRight.setDrawGridLines(false)
        axisRight.isEnabled = false
        axisRight.axisMinimum = 0f

        val xAxis = streakChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(streakDates)
        xAxis.labelCount = streakDates.size
        xAxis.isGranularityEnabled = true
        xAxis.textColor =
            ContextCompat.getColor(context, R.color.dark_theme_title)

        var params = layout.layoutParams
        params.height =
            (HabitManager.habitList[habitIndex].streaks.streaks.size * sizePerBar) + baseSize
        layout.layoutParams = params

        streakChart.invalidate()
    }

    fun updateGraph(layout: LinearLayout) {
        var habitEntries = mutableListOf<BarEntry>()
        var streakDates = mutableListOf<String>()

        for (i in 0 until HabitManager.habitList[habitIndex].streaks.streaks.size) {
            val wantedStreak = HabitManager.habitList[habitIndex].streaks.streaks[i]
            habitEntries.add(BarEntry(i.toFloat(), wantedStreak.length.toFloat()))
            streakDates.add(wantedStreak.toString())
        }

        var dataSet = BarDataSet(habitEntries, "")
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor =
            ContextCompat.getColor(context, R.color.dark_theme_title)
        dataSet.color = colour
        dataSet.valueFormatter = (object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0f", kotlin.math.floor(value))
            }
        })


        var barData = BarData(dataSet)
        streakChart.data = barData


        val xAxis = streakChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(streakDates)
        xAxis.labelCount = streakDates.size

        var params = layout.layoutParams
        params.height =
            (HabitManager.habitList[habitIndex].streaks.streaks.size * sizePerBar) + baseSize
        layout.layoutParams = params

        streakChart.invalidate()
    }

    /*
    Updates the colour of the chart based off the habit colour
     */
    fun refreshColour() {
        colour = (ColourManager.selectColour(
            HabitManager.habitList[habitIndex].colour,
            context
        ))
    }

    class LabelFormatter(var labels: Array<String>) : IndexAxisValueFormatter() {

        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return labels[value.toInt()]
        }
    }
}