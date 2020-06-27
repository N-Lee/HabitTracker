package com.nathanlee.habittracker.components

import Completion
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nathanlee.habittracker.R
import components.ColourManager

class CalendarAdapter(
    var context: Context,
    var habitIndex: Int,
    var days: MutableList<Completion>,
    var onDateListener: OnDateListener
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var dateView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_grid_layout, parent, false)
        return ViewHolder(dateView, onDateListener)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dayTimestamp = days!![position].timestamp
        var completionStatus = days!![position].status

        if (dayTimestamp.dayInt.toString() == "0") {
            holder.dateText.text = ""
        } else {
            holder.dateText.text = dayTimestamp.dayInt.toString()
        }

        setColour(completionStatus, holder.dateLayout)

    }

    private fun setColour(completionStatus: Int, view: View?) {
        when (completionStatus) {
            1 -> {
                view!!.setBackgroundColor(
                    ColourManager.selectPartialCompleteColour(
                        HabitManager.habitList[habitIndex].colour, context
                    )
                )
            }
            2, 3 -> {
                view!!.setBackgroundColor(
                    ColourManager.selectColour(
                        HabitManager.habitList[habitIndex].colour, context
                    )
                )
            }
            else -> {
                view!!.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    class ViewHolder(view: View, var onDateListener: OnDateListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var dateText: TextView = view.findViewById(R.id.calendar_date)
        var dateLayout: LinearLayout =
            view.findViewById(R.id.calendar_item_layout)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onDateListener.onDateClick(adapterPosition)
        }
    }

    interface OnDateListener {
        fun onDateClick(position: Int)
    }
}