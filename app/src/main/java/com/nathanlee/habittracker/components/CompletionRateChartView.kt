package com.nathanlee.habittracker.components

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.WindowManager
import android.widget.LinearLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nathanlee.habittracker.R
import components.ColourManager
import kotlin.math.floor

class CompletionRateChartView(
    var habitIndex: Int,
    var completionRateChart: PieChart,
    var context: Context
) {

    private var colour = (ColourManager.selectColour(
        HabitManager.habitList[habitIndex].colour,
        context
    ))

    private var groupColours = (ColourManager.selectGroupColour(
        HabitManager.habitList[habitIndex].colour,
        context
    ))

    fun createGraph(layout: LinearLayout) {

        var completionTypeCount = HabitManager.habitList[habitIndex].completionRate()
        var completionEntry = mutableListOf<PieEntry>()
        var completionTitles = mutableListOf<String>()
        completionTitles.add(context.getString(R.string.completion_rate_missed))
        completionTitles.add(context.getString(R.string.completion_rate_extra_missed))
        completionTitles.add(context.getString(R.string.completion_rate_completed))
        completionTitles.add(context.getString(R.string.completion_rate_extra))

        var chartColours = mutableListOf<Int>()

        for (i in completionTypeCount.indices) {
            if (completionTypeCount[i] != 0f) {
                chartColours.add(Color.parseColor(groupColours[completionTypeCount.size - i - 1]))
                completionEntry.add(PieEntry(completionTypeCount[i], completionTitles[i]))
            }
        }

        var dataSet = PieDataSet(completionEntry, "Number Of Completions")
        dataSet.colors = chartColours
        dataSet.valueFormatter = (object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0f", floor(value))
            }
        })

        var data = PieData(dataSet)
        data.setValueTextSize(11f)
        completionRateChart.data = data
        completionRateChart.legend.isEnabled = false
        completionRateChart.isDrawHoleEnabled = false
        completionRateChart.description.isEnabled = false
        completionRateChart.setEntryLabelColor(Color.BLACK)
        completionRateChart.setTouchEnabled(false)

        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        layout.layoutParams.height = floor(size.x / 1.5).toInt()

        completionRateChart.invalidate()
    }

    /*
    Updates the colour of the chart based off the habit colour
     */
    fun refreshColour() {
        colour = (ColourManager.selectColour(
            HabitManager.habitList[habitIndex].colour,
            context
        ))

        groupColours = (ColourManager.selectGroupColour(
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