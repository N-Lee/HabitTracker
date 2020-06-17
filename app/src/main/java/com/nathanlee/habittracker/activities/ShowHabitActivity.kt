package com.nathanlee.habittracker.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.components.CalendarView
import com.nathanlee.habittracker.components.HabitManager
import com.nathanlee.habittracker.components.HabitManager.Companion.habitList
import com.nathanlee.habittracker.components.HabitManager.Companion.todayDate
import com.nathanlee.habittracker.components.ReminderBroadcast
import com.nathanlee.habittracker.components.StreakChartView
import com.nathanlee.habittracker.models.Habit
import components.ColourManager
import java.text.SimpleDateFormat
import java.util.*

class ShowHabitActivity : AppCompatActivity(), HabitDialog.HabitDialogListener {
    private lateinit var habit: Habit
    private var habitColour = 0
    private var habitIndex = 0
    private lateinit var calendarView: CalendarView
    private lateinit var streakChartView: StreakChartView
    private lateinit var layout: LinearLayout
    private lateinit var overviewText: TextView
    private lateinit var firstDayText: TextView
    private lateinit var totalText: TextView
    private lateinit var frequencyText: TextView
    private lateinit var notificationText: TextView
    private lateinit var calendarText: TextView
    private lateinit var streakText: TextView

    private lateinit var firstDayString: SpannableString
    private lateinit var totalString: SpannableString
    private lateinit var repeatString: SpannableString
    private lateinit var notificationString: SpannableString

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_habit)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.elevation = 0f
        actionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    baseContext,
                    R.color.dark_theme_actionbar
                )
            )
        )

        if (intent.hasExtra("habit_index")) {
            habitIndex = intent.extras!!.getInt("habit_index")
            habit = habitList[habitIndex]
            habitColour = ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
            actionBar!!.title = habit.name
        } else if (intent.hasExtra("notificationHabitIndex")) {
            habitIndex = intent.extras!!.getInt("notificationHabitIndex")
            habit = habitList[habitIndex]
            habitColour = ColourManager.selectColour(
                habitList[habitIndex].colour,
                baseContext
            )
            actionBar!!.title = habit.name
        }

        layout = findViewById(R.id.chart_layout)

        overviewText = findViewById(R.id.overview_text)
        overviewText.setTextColor(habitColour)

        var overviewTextColour = ContextCompat.getColor(baseContext, R.color.dark_theme_title)

        firstDayText = findViewById(R.id.overview_first_day)
        var firstCompletion = if (habit.streaks.streaks.isEmpty()) {
            "Not started"
        } else {
            habit.streaks.streaks[0].start.toString()
        }
        var firstDayStringValue = SpannableString(firstCompletion)
        firstDayStringValue.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            firstDayStringValue.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        firstDayString = SpannableString(getString(R.string.show_habit_first_day))
        firstDayString.setSpan(
            overviewTextColour,
            0,
            firstDayString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        firstDayText.text = firstDayStringValue
        firstDayText.append(firstDayString)

        totalText = findViewById(R.id.overview_total)
        var totalStringValue = SpannableString("${habit.completions.total}")
        totalStringValue.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            totalStringValue.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        totalString = SpannableString(getString(R.string.show_habit_total))
        totalString.setSpan(
            overviewTextColour,
            0,
            totalString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        totalText.setText(totalStringValue, TextView.BufferType.SPANNABLE)
        totalText.append(totalString)

        frequencyText = findViewById(R.id.overview_frequency)
        var frequencyString =
            SpannableString("${habit.numerator} time(s) in ${habit.denominator} day(s)")
        frequencyString.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            frequencyString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        repeatString = SpannableString(getString(R.string.show_habit_repeat))
        repeatString.setSpan(
            overviewTextColour,
            0,
            repeatString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        frequencyText.setText(frequencyString, TextView.BufferType.SPANNABLE)
        frequencyText.append(repeatString)

        notificationText = findViewById(R.id.overview_notification)

        var notificationStringValue = SpannableString(getNotificationString())
        notificationStringValue.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            notificationStringValue.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        notificationString = SpannableString(getString(R.string.show_habit_notification))
        notificationString.setSpan(
            overviewTextColour,
            0,
            notificationString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        notificationText.setText(notificationStringValue, TextView.BufferType.SPANNABLE)
        notificationText.append(notificationString)

        calendarView = findViewById(R.id.calendar)
        calendarView.habitIndex = habitIndex
        calendarView.showHabitActivity = this
        calendarView.updateCalendar(todayDate)

        calendarText = findViewById(R.id.history_text)
        calendarText.setTextColor(habitColour)

        val chart: BarChart = findViewById(R.id.streak_chart)
        streakChartView = StreakChartView(habitIndex, chart, this@ShowHabitActivity)
        streakChartView.createGraph(layout)

        streakText = findViewById(R.id.streak_text)
        streakText.setTextColor(habitColour)

        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.show_habit, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                val homeIntent = Intent(this, MainActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(homeIntent)
            }
            R.id.edit_habit_app_bar -> {
                openHabitDialog()
            }
            R.id.delete_habit_app_bar -> {
                openConfirmationDialog()
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    /*
    Retrieves habit info after creating a new habit through the dialog box
     */
    override fun sendHabit(habit: Habit) {
        habitList[habitIndex].replace(
            habit.name,
            habit.description,
            habit.colour,
            habit.numerator,
            habit.denominator,
            habit.notification,
            habit.notificationTime,
            habit.notificationDays
        )
        habitList[habitIndex].updatePeriodCompletions(habitList[habitIndex].completions.completions.first().timestamp)
        habitList[habitIndex].updatePeriodStreak(habitList[habitIndex].completions.completions.first().timestamp)
        HabitManager.rw.write(habitList)

        val actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title = habit.name
        calendarView.updateCalendar(todayDate)
        habitColour = ColourManager.selectColour(
            habitList[habitIndex].colour,
            baseContext
        )
        updateStats()
    }

    /*
    Opens the dialog to create a new habit
     */
    private fun openHabitDialog() {
        var newDialog = HabitDialog(false, habitIndex, this)
        newDialog.show(supportFragmentManager, "Show models.Habit")
    }

    private fun openConfirmationDialog() {
        var newDialog = ConfirmationDialog(this)
        newDialog.show(supportFragmentManager, "Show models.Habit")
    }

    private fun updateStats() {
        overviewText.setTextColor(habitColour)

        frequencyText = findViewById(R.id.overview_frequency)
        var frequencyString =
            SpannableString("${habit.numerator} time(s) in ${habit.denominator} day(s)")
        frequencyString.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            frequencyString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        frequencyText.setText(frequencyString, TextView.BufferType.SPANNABLE)
        frequencyText.append(repeatString)

        notificationText = findViewById(R.id.overview_notification)
        var notificationStringValue = SpannableString(getNotificationString())
        notificationStringValue.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            notificationStringValue.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        notificationText.setText(notificationStringValue, TextView.BufferType.SPANNABLE)
        notificationText.append(notificationString)

        calendarText.setTextColor(habitColour)
        streakText.setTextColor(habitColour)

        updateCompletionText()
    }

    fun updateCompletionText() {
        firstDayText = findViewById(R.id.overview_first_day)
        var firstCompletion = if (habit.streaks.streaks.isEmpty()) {
            "Not started"
        } else {
            habit.streaks.streaks[0].start.toString()
        }
        var firstDayStringValue = SpannableString(firstCompletion)
        firstDayStringValue.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            firstDayStringValue.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        firstDayText.text = firstDayStringValue
        firstDayText.append(firstDayString)

        totalText = findViewById(R.id.overview_total)
        var totalStringValue = SpannableString("${habit.completions.total}")
        totalStringValue.setSpan(
            ForegroundColorSpan(habitColour),
            0,
            totalStringValue.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        totalText.setText(totalStringValue, TextView.BufferType.SPANNABLE)
        totalText.append(totalString)

        streakChartView.refreshColour()
        streakChartView.updateGraph(layout)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var name: CharSequence = "HabitNotificationChannel"
            var description = "Channel for Habit reminder"
            var importance = NotificationManager.IMPORTANCE_DEFAULT
            var channel = NotificationChannel("habitReminder", name, importance)
            channel.description = description

            var notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun delete() {
        for (i in habitList[habitIndex].notificationDays.indices) {
            if (habitList[habitIndex].notificationDays[i]) {
                var alarmManager =
                    this!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                var alarmIntent = Intent(this, ReminderBroadcast::class.java)
                var pendingIntent =
                    PendingIntent.getBroadcast(applicationContext, habit.id + i, alarmIntent, 0)
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    Log.e("Show Habit:", "Deleted intent")
                }
            }
        }

        if (!HabitManager.editLock) {
            HabitManager.editLock = true
            habitList.removeAt(habitIndex)
            HabitManager.rw.write(habitList)
            HabitManager.editLock = false
        }

        var startIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(startIntent)
    }

    /*
    fun notificationTest(){
        var alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager
        var alarmIntent = Intent(this, ReminderBroadcast::class.java).apply {

        }

        var pendingIntent =
            PendingIntent.getBroadcast(applicationContext, habit.id + i, alarmIntent, 0)

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 3000,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }

     */

    fun notificationPopUp(
        notification: Boolean,
        notificationDays: BooleanArray,
        notificationTime: String
    ) {

        var alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager
        var alarmIntent = Intent(this, ReminderBroadcast::class.java).apply {
            putExtra("notificationHabitIndex", habitIndex)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        if (notification) {
            for (i in notificationDays.indices) {

                var pendingIntent =
                    PendingIntent.getBroadcast(applicationContext, habit.id + i, alarmIntent, 0)

                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                }

                if (notificationDays[i]) {
                    val dateFormat = SimpleDateFormat("HH:mm")
                    var dateObject = dateFormat.parse(notificationTime)
                    var dateHour = notificationTime.substring(0, 2).toInt()
                    var dateMinute = notificationTime.substring(3, 5).toInt()

                    var calendar = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK, i + 1)
                        set(Calendar.HOUR_OF_DAY, dateHour)
                        set(Calendar.MINUTE, dateMinute)
                    }

                    Log.d("Date", calendar.time.toString())

                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent
                    )

                }
            }
        } else {
            for (i in notificationDays.indices) {
                if (notificationDays[i]) {
                    var pendingIntent =
                        PendingIntent.getBroadcast(applicationContext, habit.id + i, alarmIntent, 0)
                    if (pendingIntent != null) {
                        alarmManager.cancel(pendingIntent)
                    }
                }
            }
        }
    }

    fun getNotificationString(): String{
        var firstItem = true
        var notifString = ""
        var count = 0
        for (i in habit.notificationDays.indices) {
            if (habit.notificationDays[i]) {
                when (i) {
                    0 -> notifString += getString(R.string.sunday)
                    1 -> notifString += getString(R.string.monday)
                    2 -> notifString += getString(R.string.tuesday)
                    3 -> notifString += getString(R.string.wednesday)
                    4 -> notifString += getString(R.string.thursday)
                    5 -> notifString += getString(R.string.friday)
                    6 -> notifString += getString(R.string.saturday)
                }
                if (firstItem) {
                    firstItem = false
                } else {
                    notifString += ", "
                }
                count++
            }
        }

        if (count == 1) {
            notifString = notifString.substring(0, notifString.length - 1)
        } else if (count == 0) {
            notifString = "Off"
        }

        if (count == 7) {
            notifString = getString(R.string.everyday)
        }

        notifString += " at " + habit.notificationTime

        return notifString
    }
}
