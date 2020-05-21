package com.nathanlee.habittracker.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.nathanlee.habittracker.R

class ConfirmationDialog(var showHabitActivity: ShowHabitActivity) : AppCompatDialogFragment() {
    private lateinit var cancelTextView: TextView
    private lateinit var deleteTextView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.confirmation_popup, null)

        builder.setView(view)

        cancelTextView = view.findViewById(R.id.cancel_text)
        cancelTextView.setOnClickListener {
            this.dismiss()
        }

        deleteTextView = view.findViewById(R.id.delete_text)
        deleteTextView.setOnClickListener{
            deleteHabit()
        }

        return builder.create()
    }

    private fun deleteHabit() {
        showHabitActivity.delete()
        this.dismiss()
    }
}