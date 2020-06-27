package com.nathanlee.habittracker.activities

import Completion
import Timestamp
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.components.HabitManager.Companion.editLock
import com.nathanlee.habittracker.components.HabitManager.Companion.habitList
import com.nathanlee.habittracker.components.HabitManager.Companion.managerDate
import com.nathanlee.habittracker.components.HabitManager.Companion.nextId
import com.nathanlee.habittracker.components.HabitManager.Companion.rw
import com.nathanlee.habittracker.components.HabitManager.Companion.simpleDate
import com.nathanlee.habittracker.components.HabitManager.Companion.todayDate
import com.nathanlee.habittracker.components.HorizontalScroll
import com.nathanlee.habittracker.components.ReminderBroadcast
import com.nathanlee.habittracker.components.VerticalScroll
import com.nathanlee.habittracker.models.Habit
import components.ColourManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class MainActivity : AppCompatActivity(), HabitDialog.HabitDialogListener,
    VerticalScroll.ScrollViewListener, HorizontalScroll.ScrollViewListener {

    private val NUMBER_OF_DATES = 12

    private val ROW_MARGIN = 15

    private var CELL_WIDTH = 200

    private var screenHeight = 0
    private var screenWidth = 0

    private var leftRelativeLayoutWidth = 0
    private var rightRelativeLayoutWidth = 0
    private var topRelativeLayoutHeight = 0
    private var bottomRelativeLayoutHeight = 0

    private var currentX = 0
    private var previousX = 0
    private var currentY = 0
    private var previousY = 0

    private var dateList = mutableListOf<Timestamp>()
    private lateinit var habitDialog: Dialog

    private lateinit var mainRelativeLayout: RelativeLayout
    private lateinit var headerRelativeLayout: RelativeLayout
    private lateinit var columnHeaderRelativeLayout: RelativeLayout
    private lateinit var rowHeaderRelativeLayout: RelativeLayout
    private lateinit var tableRelativeLayout: RelativeLayout

    private lateinit var headerTableLayout: TableLayout
    private lateinit var columnHeaderTableLayout: TableLayout
    private lateinit var rowHeaderTableLayout: TableLayout
    private lateinit var tableTableLayout: TableLayout

    private lateinit var tableRow: TableRow
    private lateinit var tableRowB: TableRow

    private lateinit var columnHeaderHorizontalScrollView: HorizontalScroll
    private lateinit var tableHorizontalScrollView: HorizontalScroll

    private lateinit var rowHeaderScrollView: VerticalScroll
    private lateinit var tableScrollView: VerticalScroll

    private lateinit var editHabitListener: View.OnClickListener
    private lateinit var editStatusListener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = "Habit"
        actionBar.elevation = 0f
        actionBar.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    baseContext,
                    R.color.dark_theme_actionbar
                )
            )
        )

        habitDialog = Dialog(this)

        editHabitListener = View.OnClickListener { view ->
            var startIntent = Intent(applicationContext, ShowHabitActivity::class.java)
            var rowIndex = view.tag as Int
            startIntent.putExtra("habit_index", rowIndex)
            startActivity(startIntent)
        }

        editStatusListener = View.OnClickListener { view ->
            if (!editLock) {
                editLock = true
                val tags: String = view.tag as String
                val pos: Array<String> = tags.split(",").toTypedArray()
                val wantedHabit = habitList[pos[0].toInt()]
                val wantedDate = dateList[pos[1].toInt()]
                val completionsList = wantedHabit.completions
                val wantedCompletion =
                    completionsList.completions[completionsList.find(
                        0,
                        completionsList.completions.size - 1,
                        wantedDate
                    )]

                if (wantedCompletion.status == 2 || wantedCompletion.status == 3) {
                    wantedHabit.editDate(wantedDate, 0)
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.dark_theme_table_background
                        )
                    )
                } else {
                    wantedHabit.editDate(wantedDate, 2)
                    view.setBackgroundColor(
                        ColourManager.selectColour(
                            wantedHabit.colour,
                            this
                        )
                    )
                }

                rw.write(habitList)
                editLock = false
            }
        }

        mainRelativeLayout = findViewById(R.id.main_relative_layout)
        getDimension()
        initializeRelativeLayout()
        initializeScroll()
        initializeTableLayout()
        columnHeaderHorizontalScrollView.setScrollViewListener(this)
        tableHorizontalScrollView.setScrollViewListener(this)
        rowHeaderScrollView.setScrollViewListener(this)
        tableScrollView.setScrollViewListener(this)
        createHeader()
        initializeColumnHeaderTable()
        createEmptyRow()

        for (i in 0 until habitList.size) {
            initializeRowForTable(i)
            addRowToRowHeader(habitList[i], i)
            for (j in 0 until dateList.size) {
                addCellToTable(i, j, habitList[i])
            }
        }

        createNotificationChannel()

        tableHorizontalScrollView.viewTreeObserver
            .addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        tableHorizontalScrollView.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)
                        tableHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                    }
                })

        columnHeaderHorizontalScrollView.viewTreeObserver
            .addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {

                    override fun onGlobalLayout() {
                        columnHeaderHorizontalScrollView.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)
                        columnHeaderHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                    }
                })

        val onTouchListener: View.OnTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (currentX > previousX) {
                        columnHeaderHorizontalScrollView.smoothScrollTo(
                            (ceil(currentX.toDouble() / CELL_WIDTH) * CELL_WIDTH).toInt(),
                            currentY
                        )
                        tableHorizontalScrollView.smoothScrollTo(
                            (ceil(currentX.toDouble() / CELL_WIDTH) * CELL_WIDTH).toInt(),
                            currentY
                        )
                    } else {
                        columnHeaderHorizontalScrollView.smoothScrollTo(
                            (floor(currentX.toDouble() / CELL_WIDTH) * CELL_WIDTH).toInt(),
                            currentY
                        )
                        tableHorizontalScrollView.smoothScrollTo(
                            (floor(currentX.toDouble() / CELL_WIDTH) * CELL_WIDTH).toInt(),
                            currentY
                        )
                    }
                }
            }
            false
        }

        columnHeaderHorizontalScrollView.setOnTouchListener(onTouchListener)
        tableHorizontalScrollView.setOnTouchListener(onTouchListener)
        loadData()
    }

    override fun onResume() {
        super.onResume()

        managerDate = Date()
        simpleDate = SimpleDateFormat("dd/MM/yyyy").format(managerDate)
        todayDate = Timestamp(simpleDate)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_habit_app_bar -> {
                openHabitDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /*
    Retrieves habit info after creating a new habit through the dialog box
     */
    override fun sendHabit(habit: Habit) {
        initializeRowForTable(habitList.size)

        habit.completions.edit(Completion(dateList[0], 0), 1)
        habit.completions.edit(Completion(dateList.last(), 0), 1)

        addRowToRowHeader(habit, habitList.size)
        for (j in 0 until dateList.size) {
            addCellToTable(habitList.size, j, habit)
        }

        columnHeaderHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)

        habitList.add(habit)
        rw.write(habitList)
        nextId += 10
        saveData()
    }

    override fun onScrollChanged(
        scrollView: HorizontalScroll,
        x: Int,
        y: Int,
        oldX: Int,
        oldY: Int
    ) {
        if (scrollView == columnHeaderHorizontalScrollView) {
            tableHorizontalScrollView.scrollTo(x, y)
        } else if (scrollView == tableHorizontalScrollView) {
            columnHeaderHorizontalScrollView.scrollTo(x, y)
        }

        currentX = x
        previousX = oldX
        currentY = y
        previousY = oldY
    }

    override fun onScrollChanged(scrollView: VerticalScroll, x: Int, y: Int, oldX: Int, oldY: Int) {
        if (scrollView == rowHeaderScrollView) {
            tableScrollView.scrollTo(x, y)
        } else if (scrollView == tableScrollView) {
            rowHeaderScrollView.scrollTo(x, y)
        }
    }

    /*
    Opens the dialog to create a new habit
     */
    private fun openHabitDialog() {
        var newDialog = HabitDialog(true, habitList.size, this)
        newDialog.show(supportFragmentManager, "Show models.Habit")
    }


    /*
    Gets the dimension of the screen and determines how much space each cell will take in the table
     */
    private fun getDimension() {
        val wm =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        screenWidth = size.x
        screenHeight = size.y
        leftRelativeLayoutWidth = screenWidth / 3
        rightRelativeLayoutWidth = screenWidth - leftRelativeLayoutWidth
        topRelativeLayoutHeight = screenHeight / 13
        bottomRelativeLayoutHeight = screenHeight - topRelativeLayoutHeight
        CELL_WIDTH = rightRelativeLayoutWidth / 4
    }

    /*
    Creates the main relative layout and places the table relative layouts into the main relative layout
     */
    private fun initializeRelativeLayout() {
        val layoutParamsColumnHeaderLayout =
            RelativeLayout.LayoutParams(rightRelativeLayoutWidth, topRelativeLayoutHeight)
        val layoutParamsRowHeaderLayout =
            RelativeLayout.LayoutParams(leftRelativeLayoutWidth, bottomRelativeLayoutHeight)
        val layoutParamsTableLayout =
            RelativeLayout.LayoutParams(rightRelativeLayoutWidth, bottomRelativeLayoutHeight)

        headerRelativeLayout = RelativeLayout(applicationContext)
        headerRelativeLayout.id = R.id.header_relative_layout
        headerRelativeLayout.setPadding(0, 0, 0, 0)

        columnHeaderRelativeLayout = RelativeLayout(applicationContext)
        columnHeaderRelativeLayout.id =
            R.id.column_header_relative_layout
        columnHeaderRelativeLayout.setPadding(0, 0, 0, 0)

        rowHeaderRelativeLayout = RelativeLayout(applicationContext)
        rowHeaderRelativeLayout.id = R.id.row_header_relative_layout
        rowHeaderRelativeLayout.setPadding(0, 0, 0, 0)

        tableRelativeLayout = RelativeLayout(applicationContext)
        tableRelativeLayout.id = R.id.table_relative_layout
        tableRelativeLayout.setPadding(0, 0, 0, 0)

        headerRelativeLayout.layoutParams =
            RelativeLayout.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        this.mainRelativeLayout.addView(headerRelativeLayout)


        layoutParamsColumnHeaderLayout.addRule(
            RelativeLayout.RIGHT_OF,
            R.id.header_relative_layout
        )
        columnHeaderRelativeLayout.layoutParams = layoutParamsColumnHeaderLayout
        this.mainRelativeLayout.addView(columnHeaderRelativeLayout)


        layoutParamsRowHeaderLayout.addRule(
            RelativeLayout.BELOW,
            R.id.header_relative_layout
        )
        rowHeaderRelativeLayout.layoutParams = layoutParamsRowHeaderLayout
        this.mainRelativeLayout.addView(rowHeaderRelativeLayout)

        layoutParamsTableLayout.addRule(
            RelativeLayout.BELOW,
            R.id.column_header_relative_layout
        )
        layoutParamsTableLayout.addRule(
            RelativeLayout.RIGHT_OF,
            R.id.row_header_relative_layout
        )
        tableRelativeLayout.layoutParams = layoutParamsTableLayout
        this.mainRelativeLayout.addView(tableRelativeLayout)
    }

    /*
    Creates the scroll views for the table
     */
    private fun initializeScroll() {
        columnHeaderHorizontalScrollView =
            HorizontalScroll(applicationContext)
        columnHeaderHorizontalScrollView.setPadding(0, 0, 0, 0)
        columnHeaderHorizontalScrollView.isHorizontalScrollBarEnabled = false
        columnHeaderHorizontalScrollView.overScrollMode = View.OVER_SCROLL_NEVER

        tableHorizontalScrollView =
            HorizontalScroll(applicationContext)
        tableHorizontalScrollView.setPadding(0, 0, 0, 0)
        tableHorizontalScrollView.isHorizontalScrollBarEnabled = false
        tableHorizontalScrollView.overScrollMode = View.OVER_SCROLL_NEVER

        rowHeaderScrollView =
            VerticalScroll(applicationContext)
        rowHeaderScrollView.setPadding(0, 0, 0, 0)
        rowHeaderScrollView.isVerticalScrollBarEnabled = false
        rowHeaderScrollView.overScrollMode = View.OVER_SCROLL_NEVER

        tableScrollView = VerticalScroll(applicationContext)
        tableScrollView.setPadding(0, 0, 0, 0)
        tableScrollView.isHorizontalScrollBarEnabled = false
        tableScrollView.isVerticalScrollBarEnabled = false
        tableScrollView.overScrollMode = View.OVER_SCROLL_NEVER

        columnHeaderHorizontalScrollView.layoutParams =
            ViewGroup.LayoutParams(
                rightRelativeLayoutWidth,
                topRelativeLayoutHeight
            )

        rowHeaderScrollView.layoutParams =
            ViewGroup.LayoutParams(
                leftRelativeLayoutWidth,
                bottomRelativeLayoutHeight
            )

        tableScrollView.layoutParams =
            ViewGroup.LayoutParams(
                rightRelativeLayoutWidth,
                bottomRelativeLayoutHeight
            )

        tableHorizontalScrollView.layoutParams =
            ViewGroup.LayoutParams(
                rightRelativeLayoutWidth,
                bottomRelativeLayoutHeight
            )


        this.columnHeaderRelativeLayout.addView(columnHeaderHorizontalScrollView)
        this.rowHeaderRelativeLayout.addView(rowHeaderScrollView)
        this.tableScrollView.addView(tableHorizontalScrollView)
        this.tableRelativeLayout.addView(tableScrollView)
    }

    /*
    Sets the size and styles of each relative layout in the table
     */
    private fun initializeTableLayout() {
        val layoutParamsHeaderTableLayout =
            TableLayout.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        val layoutParamsColumnHeaderTableLayout =
            TableLayout.LayoutParams(rightRelativeLayoutWidth, topRelativeLayoutHeight)
        val layoutParamsRowHeaderTableLayout =
            TableLayout.LayoutParams(leftRelativeLayoutWidth, bottomRelativeLayoutHeight)
        val layoutParamsTableTableLayout = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.MATCH_PARENT
        )

        headerTableLayout = TableLayout(applicationContext)
        headerTableLayout.setPadding(0, 0, 0, 0)

        columnHeaderTableLayout = TableLayout(applicationContext)
        columnHeaderTableLayout.setPadding(0, 0, 0, 0)
        columnHeaderTableLayout.id = R.id.column_header_table_layout

        rowHeaderTableLayout = TableLayout(applicationContext)
        rowHeaderTableLayout.setPadding(0, 0, 0, 0)

        tableTableLayout = TableLayout(applicationContext)
        tableTableLayout.setPadding(0, 0, 0, 0)

        headerTableLayout.layoutParams = layoutParamsHeaderTableLayout
        headerTableLayout.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.dark_theme_background
            )
        )
        this.headerRelativeLayout.addView(headerTableLayout)

        columnHeaderTableLayout.layoutParams = layoutParamsColumnHeaderTableLayout
        columnHeaderTableLayout.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.dark_theme_background
            )
        )
        this.columnHeaderHorizontalScrollView.addView(columnHeaderTableLayout)
        rowHeaderTableLayout.layoutParams = layoutParamsRowHeaderTableLayout
        this.rowHeaderScrollView.addView(rowHeaderTableLayout)
        tableTableLayout.layoutParams = layoutParamsTableTableLayout
        this.tableHorizontalScrollView.addView(tableTableLayout)
    }

    /*
    Creates the header (where the row header and column header intersect)
     */
    private fun createHeader() {
        val layoutParamsTableRow =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        val tableName = TextView(applicationContext)

        tableName.textSize =
            resources.getDimension(R.dimen.cell_text_size)

        tableRow = TableRow(applicationContext)
        tableRow.layoutParams = layoutParamsTableRow
        tableRow.addView(tableName)
        this.headerTableLayout.addView(tableRow)
    }

    /*
    Creates the column headers for the table
     */
    private fun initializeColumnHeaderTable() {
        tableRowB = TableRow(applicationContext)
        tableRowB.setPadding(0, 0, 0, 0)
        this.columnHeaderTableLayout.addView(tableRowB)

        for (i in 0..NUMBER_OF_DATES) {
            val wantedDate = todayDate.getDaysBefore(NUMBER_OF_DATES - i)
            val weekday = wantedDate.getDayOfWeek(wantedDate)
            val today = wantedDate.dayInt

            addColumnsToColumnHeader("$weekday\n$today", i)
            dateList.add(wantedDate)
        }
    }

    /*
    Add a cell to the column header
     */
    @Synchronized
    private fun addColumnsToColumnHeader(text: String, id: Int) {
        val newTableRow = TableRow(applicationContext).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        val layoutParamsTableRow =
            TableRow.LayoutParams(CELL_WIDTH, topRelativeLayoutHeight)
        val headerTextView = TextView(applicationContext)

        newTableRow.setPadding(3, 3, 3, 4)
        newTableRow.layoutParams = layoutParamsTableRow

        headerTextView.text = text
        headerTextView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        headerTextView.textSize =
            resources.getDimension(R.dimen.header_cell_text_size)
        headerTextView.setTextColor(resources.getColor(R.color.dark_theme_title))

        newTableRow.addView(headerTextView)
        newTableRow.tag = id

        tableRowB.addView(newTableRow)
    }

    /*
    Adds a cell to the row header
     */
    @Synchronized
    private fun addRowToRowHeader(habit: Habit, rowPos: Int) {
        val newTableRow = TableRow(applicationContext).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        val layoutParamsTableRow1 =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        val headerName = TextView(applicationContext)
        val outerTableRow = TableRow(applicationContext)
        val layoutParamsTableRow =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)

        layoutParamsTableRow1.setMargins(0, 0, 0, ROW_MARGIN)

        newTableRow.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.dark_theme_table_background
            )
        )
        newTableRow.setPadding(3, 3, 3, 4)
        newTableRow.layoutParams = layoutParamsTableRow1

        headerName.setPadding(20, 0, 0, 0)
        headerName.text = habit.name
        headerName.textSize =
            resources.getDimension(R.dimen.cell_text_size)
        headerName.setTextColor(
            ColourManager.selectColour(
                habit.colour,
                this
            )
        )
        newTableRow.tag = rowPos
        newTableRow.setOnClickListener(editHabitListener)

        newTableRow.addView(headerName)

        outerTableRow.setPadding(0, 0, 0, 0)
        outerTableRow.layoutParams = layoutParamsTableRow
        outerTableRow.addView(newTableRow)

        this.rowHeaderTableLayout.addView(outerTableRow, rowPos)
    }

    /*
    Creates a row
     */
    @Synchronized
    private fun initializeRowForTable(pos: Int) {
        val newTableRow = TableRow(applicationContext)
        val layoutParamsTableRow =
            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, topRelativeLayoutHeight)

        newTableRow.setPadding(0, 0, 0, 0)
        newTableRow.layoutParams = layoutParamsTableRow

        this.tableTableLayout.addView(newTableRow, pos)

    }

    /*
    Adds a cell to the table
     */
    @Synchronized
    private fun addCellToTable(currentRow: Int, currentColumn: Int, habit: Habit) {
        val currentTableRow = this.tableTableLayout.getChildAt(currentRow) as TableRow
        val layoutParamsTableRow =
            TableRow.LayoutParams(CELL_WIDTH, topRelativeLayoutHeight)
        val habitStatus = TextView(applicationContext)
        val completionsList = habit.completions
        val wantedCompletion =
            completionsList.completions[completionsList.find(
                0,
                completionsList.completions.size - 1,
                dateList[currentColumn]
            )]

        layoutParamsTableRow.setMargins(0, 0, 0, ROW_MARGIN)

        tableRow = TableRow(applicationContext)
        tableRow.setPadding(3, 3, 3, 4)
        tableRow.layoutParams = layoutParamsTableRow

        if (wantedCompletion.status == 2 || wantedCompletion.status == 3) {
            tableRow.setBackgroundColor(
                ColourManager.selectColour(
                    habit.colour,
                    this
                )
            )
        } else {
            tableRow.setBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.dark_theme_table_background
                )
            )
        }

        tableRow.tag = "$currentRow,$currentColumn"
        tableRow.setOnClickListener(editStatusListener)
        this.tableRow.addView(habitStatus)
        currentTableRow.addView(tableRow)
    }

    /*
    Creates a blank row
     */
    private fun createEmptyRow() {
        val newTableRow = TableRow(applicationContext)
        val layoutParamsNewTableRow =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)

        newTableRow.setPadding(0, 0, 0, 0)
        newTableRow.layoutParams = layoutParamsNewTableRow

        this.rowHeaderTableLayout.addView(newTableRow, 0)

        initializeRowForTable(0)

        for (i in 0 until dateList.size) {
            val currentTableRow = this.tableTableLayout.getChildAt(0) as TableRow
            val layoutParamsTableRow =
                TableRow.LayoutParams(CELL_WIDTH, 0)
            val habitStatus = TextView(applicationContext)

            tableRow = TableRow(applicationContext)
            tableRow.setPadding(0, 0, 0, 0)
            tableRow.layoutParams = layoutParamsTableRow

            this.tableRow.addView(habitStatus)
            currentTableRow.addView(tableRow)
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("habitID", nextId)
        editor.apply()
    }

    /*
    Loads Shared Preferences
     */
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        nextId = sharedPreferences.getInt("habitID", 0)
    }

    /*
    Creates a notification channel if the device OS is above Oreo
     */
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

    /*
    Sets up notification for the newly created habit
     */
    fun notificationPopUp(
        notification: Boolean,
        notificationDays: BooleanArray,
        notificationTime: String,
        habitIndex: Int
    ) {

        var habit = habitList[habitIndex]
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
                    var dateHour = notificationTime.substring(0, 2).toInt()
                    var dateMinute = notificationTime.substring(3, 5).toInt()

                    var calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, dateHour)
                        set(Calendar.MINUTE, dateMinute)
                        set(Calendar.SECOND, 0)
                    }

                    while (calendar.get(Calendar.DAY_OF_WEEK) != i + 1) {
                        calendar.add(Calendar.DATE, 1)
                    }

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
}
