package com.nathanlee.habittracker.activities

import Completion
import ReadWriteJson
import Timestamp
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nathanlee.habittracker.models.ColourManager
import com.nathanlee.habittracker.models.Habit
import java.text.SimpleDateFormat
import java.util.*

// TODO: Make the completions toggle in the table

class MainActivity : AppCompatActivity(), HabitDialog.HabitDialogListener,
    VerticalScroll.ScrollViewListener, HorizontalScroll.ScrollViewListener {

    val DATE = Date()
    val SIMPLE_DATE = SimpleDateFormat("dd/MM/yyyy").format(DATE)
    val TIMESTAMP = Timestamp(SIMPLE_DATE)

    val NUMBER_OF_DATES = 12

    var CELL_WIDTH = 200

    var screenHeight = 0
    var screenWidth = 0

    var leftRelativeLayoutWidth = 0
    var rightRelativeLayoutWidth = 0
    var topRelativeLayoutHeight = 0
    var bottomRelativeLayoutHeight = 0

    var mainActionBar: ActionBar? = null

    var dateList = mutableListOf<Timestamp>()
    var habitList = mutableListOf<Habit>()

    lateinit var rw: ReadWriteJson

    lateinit var habitDialog: Dialog

    lateinit var mainRelativeLayout: RelativeLayout
    lateinit var headerRelativeLayout: RelativeLayout
    lateinit var columnHeaderRelativeLayout: RelativeLayout
    lateinit var rowHeaderRelativeLayout: RelativeLayout
    lateinit var tableRelativeLayout: RelativeLayout

    lateinit var headerTableLayout: TableLayout
    lateinit var columnHeaderTableLayout: TableLayout
    lateinit var rowHeaderTableLayout: TableLayout
    lateinit var tableTableLayout: TableLayout

    lateinit var tableRow: TableRow
    lateinit var tableRowB: TableRow

    lateinit var columnHeaderHorizontalScrollView: HorizontalScroll
    lateinit var tableHorizontalScrollView: HorizontalScroll

    lateinit var rowHeaderScrollView: VerticalScroll
    lateinit var tableScrollView: VerticalScroll

    lateinit var editHabitListener: View.OnClickListener
    lateinit var editStatusListener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nathanlee.habittracker.R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = "Habit"

        habitDialog = Dialog(this)

        editHabitListener = View.OnClickListener { view ->
            var startIntent = Intent(applicationContext, EditHabitActivity::class.java)
            var rowIndex = view.tag as Int
            startIntent.putExtra("edit_habit", habitList[rowIndex])
            startActivity(startIntent)
        }

        editStatusListener = View.OnClickListener { view ->
            val tags: String = view.tag as String
            val pos: Array<String> = tags.split(",").toTypedArray()
            val wantedHabit = habitList[pos[0].toInt()]
            val wantedDate = dateList[pos[1].toInt()]
            val completionsList = wantedHabit.completions
            val wantedCompletion =
                completionsList.completions[completionsList.find(0, completionsList.completions.size, wantedDate)]

            if (wantedCompletion.status == 2 || wantedCompletion.status == 3) {
                val newCompletion = Completion(wantedDate, 0)
                completionsList.edit(newCompletion, 0)
                view.setBackgroundColor(Color.TRANSPARENT)
            } else {
                val newCompletion = Completion(wantedDate, 2)
                completionsList.edit(newCompletion, 0)
                view.setBackgroundColor(
                    ColourManager.selectColour(
                        wantedHabit.colour,
                        this
                    )
                )
            }
        }

        mainRelativeLayout = findViewById(com.nathanlee.habittracker.R.id.mainRelativeLayout)
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
        initializeHabits()

        for (i in 0 until habitList.size) {
            initializeRowForTable(i)
            addRowToRowHeader(habitList[i])
            for (j in 0 until dateList.size) {
                addColumnToTable(i, j, habitList[i])
            }
        }

        /*
        for (i in 0..5) {
            initializeRowForTable(i)
            val newHabit = Habit("Habit $i", "Description $i", COLOURS[i], 7, 7)
            habitList.add(newHabit)
            addRowToRowHeader(habitList[i])
            for (j in 0 until dateList.size) {
                addColumnToTable(habitList.size - 1, j, habitList[i])
            }
        }

         */

        tableHorizontalScrollView.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    tableHorizontalScrollView.viewTreeObserver
                        .removeOnGlobalLayoutListener(this)
                    tableHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                }
            })

        columnHeaderHorizontalScrollView.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    columnHeaderHorizontalScrollView.viewTreeObserver
                        .removeOnGlobalLayoutListener(this)
                    columnHeaderHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                }
            })

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.nathanlee.habittracker.R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.nathanlee.habittracker.R.id.addHabitAppBar -> {
                openHabitDialog()
            }

            com.nathanlee.habittracker.R.id.settingsAppBar -> {
                var startIntent = Intent(applicationContext, SettingsActivity::class.java)
                startIntent.putExtra("com.nathanlee.habittracker.SOMETHING", "Text has changed")
                startActivity(startIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHabitDialog() {
        var newDialog = HabitDialog()
        newDialog.show(supportFragmentManager, "Show com.nathanlee.habittracker.models.Habit")
    }

    override fun sendHabit(habit: Habit) {
        initializeRowForTable(habitList.size)
        habitList.add(habit)
        addRowToRowHeader(habit)
        for (j in 0 until dateList.size) {
            addColumnToTable(habitList.size - 1, j, habitList[habitList.size - 1])
        }
    }

    private fun getDimension() {
        val wm =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        screenWidth = size.x
        screenHeight = size.y
        leftRelativeLayoutWidth = screenWidth / 5
        rightRelativeLayoutWidth = screenWidth - leftRelativeLayoutWidth
        topRelativeLayoutHeight = screenHeight / 20
        bottomRelativeLayoutHeight = screenHeight - topRelativeLayoutHeight
        CELL_WIDTH = rightRelativeLayoutWidth / 5
    }

    fun initializeRelativeLayout() {
        val layoutParamsColumnHeaderLayout =
            RelativeLayout.LayoutParams(rightRelativeLayoutWidth, topRelativeLayoutHeight)
        val layoutParamsRowHeaderLayout =
            RelativeLayout.LayoutParams(leftRelativeLayoutWidth, bottomRelativeLayoutHeight)
        val layoutParamsTableLayout =
            RelativeLayout.LayoutParams(rightRelativeLayoutWidth, bottomRelativeLayoutHeight)

        headerRelativeLayout = RelativeLayout(applicationContext)
        headerRelativeLayout.id = com.nathanlee.habittracker.R.id.header_relative_layout
        headerRelativeLayout.setPadding(0, 0, 0, 0)

        columnHeaderRelativeLayout = RelativeLayout(applicationContext)
        columnHeaderRelativeLayout.id =
            com.nathanlee.habittracker.R.id.column_header_relative_layout
        columnHeaderRelativeLayout.setPadding(0, 0, 0, 0)

        rowHeaderRelativeLayout = RelativeLayout(applicationContext)
        rowHeaderRelativeLayout.id = com.nathanlee.habittracker.R.id.row_header_relative_layout
        rowHeaderRelativeLayout.setPadding(0, 0, 0, 0)

        tableRelativeLayout = RelativeLayout(applicationContext)
        tableRelativeLayout.id = com.nathanlee.habittracker.R.id.table_relative_layout
        tableRelativeLayout.setPadding(0, 0, 0, 0)

        headerRelativeLayout.layoutParams =
            RelativeLayout.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        this.mainRelativeLayout.addView(headerRelativeLayout)


        layoutParamsColumnHeaderLayout.addRule(
            RelativeLayout.RIGHT_OF,
            com.nathanlee.habittracker.R.id.header_relative_layout
        )
        columnHeaderRelativeLayout.layoutParams = layoutParamsColumnHeaderLayout
        this.mainRelativeLayout.addView(columnHeaderRelativeLayout)


        layoutParamsRowHeaderLayout.addRule(
            RelativeLayout.BELOW,
            com.nathanlee.habittracker.R.id.header_relative_layout
        )
        rowHeaderRelativeLayout.layoutParams = layoutParamsRowHeaderLayout
        this.mainRelativeLayout.addView(rowHeaderRelativeLayout)

        layoutParamsTableLayout.addRule(
            RelativeLayout.BELOW,
            com.nathanlee.habittracker.R.id.column_header_relative_layout
        )
        layoutParamsTableLayout.addRule(
            RelativeLayout.RIGHT_OF,
            com.nathanlee.habittracker.R.id.row_header_relative_layout
        )
        tableRelativeLayout.layoutParams = layoutParamsTableLayout
        this.mainRelativeLayout.addView(tableRelativeLayout)
    }

    fun initializeScroll() {
        columnHeaderHorizontalScrollView = HorizontalScroll(applicationContext)
        columnHeaderHorizontalScrollView.setPadding(0, 0, 0, 0)
        columnHeaderHorizontalScrollView.isHorizontalScrollBarEnabled = false
        columnHeaderHorizontalScrollView.overScrollMode = View.OVER_SCROLL_NEVER

        tableHorizontalScrollView = HorizontalScroll(applicationContext)
        tableHorizontalScrollView.setPadding(0, 0, 0, 0)
        tableHorizontalScrollView.isHorizontalScrollBarEnabled = false
        tableHorizontalScrollView.overScrollMode = View.OVER_SCROLL_NEVER

        rowHeaderScrollView = VerticalScroll(applicationContext)
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

    fun initializeTableLayout() {
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
        columnHeaderTableLayout.id = com.nathanlee.habittracker.R.id.column_header_table_layout

        rowHeaderTableLayout = TableLayout(applicationContext)
        rowHeaderTableLayout.setPadding(0, 0, 0, 0)

        tableTableLayout = TableLayout(applicationContext)
        tableTableLayout.setPadding(0, 0, 0, 0)

        headerTableLayout.layoutParams = layoutParamsHeaderTableLayout
        headerTableLayout.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.nathanlee.habittracker.R.color.darkThemeTableBackground
            )
        )
        this.headerRelativeLayout.addView(headerTableLayout)

        columnHeaderTableLayout.layoutParams = layoutParamsColumnHeaderTableLayout
        columnHeaderTableLayout.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.nathanlee.habittracker.R.color.darkThemeTableBackground
            )
        )
        this.columnHeaderHorizontalScrollView.addView(columnHeaderTableLayout)

        rowHeaderTableLayout.layoutParams = layoutParamsRowHeaderTableLayout
        rowHeaderTableLayout.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.nathanlee.habittracker.R.color.darkThemeBackground
            )
        )
        this.rowHeaderScrollView.addView(rowHeaderTableLayout)

        tableTableLayout.layoutParams = layoutParamsTableTableLayout
        tableTableLayout.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                com.nathanlee.habittracker.R.color.darkThemeBackground
            )
        )
        this.tableHorizontalScrollView.addView(tableTableLayout)
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
    }

    override fun onScrollChanged(scrollView: VerticalScroll, x: Int, y: Int, oldX: Int, oldY: Int) {
        if (scrollView == rowHeaderScrollView) {
            tableScrollView.scrollTo(x, y)
        } else if (scrollView == tableScrollView) {
            rowHeaderScrollView.scrollTo(x, y)
        }
    }

    fun createHeader() {
        val layoutParamsTableRow =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        val TABLE_NAME = TextView(applicationContext)

        TABLE_NAME.setText(com.nathanlee.habittracker.R.string.header_table)
        TABLE_NAME.textSize =
            resources.getDimension(com.nathanlee.habittracker.R.dimen.cell_text_size)

        tableRow = TableRow(applicationContext)
        tableRow.layoutParams = layoutParamsTableRow
        tableRow.addView(TABLE_NAME)
        this.headerTableLayout.addView(tableRow)
    }

    fun initializeColumnHeaderTable() {
        tableRowB = TableRow(applicationContext)
        tableRowB.setPadding(0, 0, 0, 0)
        this.columnHeaderTableLayout.addView(tableRowB)

        for (i in 0..NUMBER_OF_DATES) {
            val WANTED_DATE = TIMESTAMP.getDaysBefore(NUMBER_OF_DATES - i)
            val WEEKDAY = WANTED_DATE.getDayOfWeek(WANTED_DATE)
            val TODAY = WANTED_DATE.dayInt

            addColumnsToColumnHeader("$WEEKDAY\n$TODAY", i)
            dateList.add(WANTED_DATE)
        }
    }

    @Synchronized
    private fun addColumnsToColumnHeader(text: String, id: Int) {
        val newTableRow = TableRow(applicationContext)
        val layoutParamsTableRow =
            TableRow.LayoutParams(CELL_WIDTH, topRelativeLayoutHeight)
        val HEADER_TEXT_VIEW = TextView(applicationContext)

        newTableRow.setPadding(3, 3, 3, 4)
        newTableRow.layoutParams = layoutParamsTableRow

        HEADER_TEXT_VIEW.text = text
        HEADER_TEXT_VIEW.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        HEADER_TEXT_VIEW.textSize =
            resources.getDimension(com.nathanlee.habittracker.R.dimen.header_cell_text_size)

        newTableRow.addView(HEADER_TEXT_VIEW)
        newTableRow.tag = id

        tableRowB.addView(newTableRow)
    }

    @Synchronized
    private fun addRowToRowHeader(habit: Habit) {
        val newTableRow = TableRow(applicationContext)
        val layoutParamsTableRow1 =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)
        val headerName = TextView(applicationContext)
        val outerTableRow = TableRow(applicationContext)
        val layoutParamsTableRow =
            TableRow.LayoutParams(leftRelativeLayoutWidth, topRelativeLayoutHeight)

        newTableRow.setPadding(3, 3, 3, 4)
        newTableRow.layoutParams = layoutParamsTableRow1

        headerName.text = habit.name
        headerName.textSize =
            resources.getDimension(com.nathanlee.habittracker.R.dimen.cell_text_size)
        headerName.setTextColor(
            ColourManager.selectColour(
                habit.colour,
                this
            )
        )
        headerName.tag = habitList.size - 1
        headerName.setOnClickListener(editHabitListener)

        newTableRow.addView(headerName)

        outerTableRow.setPadding(0, 0, 0, 0)
        outerTableRow.layoutParams = layoutParamsTableRow
        outerTableRow.addView(newTableRow)

        this.rowHeaderTableLayout.addView(outerTableRow, habitList.size - 1)
    }

    @Synchronized
    private fun initializeRowForTable(pos: Int) {
        val newTableRow = TableRow(applicationContext)
        val layoutParamsTableRow =
            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, topRelativeLayoutHeight)

        newTableRow.setPadding(0, 0, 0, 0)
        newTableRow.layoutParams = layoutParamsTableRow
        this.tableTableLayout.addView(newTableRow, pos)
    }

    @Synchronized
    private fun addColumnToTable(currentRow: Int, currentColumn: Int, habit: Habit) {
        val currentTableRow = this.tableTableLayout.getChildAt(currentRow) as TableRow
        val layoutParamsTableRow =
            TableRow.LayoutParams(CELL_WIDTH, topRelativeLayoutHeight)
        val habitStatus = TextView(applicationContext)
        val completionsList = habit.completions
        val wantedCompletion =
            completionsList.completions[completionsList.find(0, completionsList.completions.size, dateList[currentColumn])]

        tableRow = TableRow(applicationContext)
        tableRow.setPadding(3, 3, 3, 4)
        tableRow.layoutParams = layoutParamsTableRow

        // TODO: Need to test this
        if (wantedCompletion.status == 2 || wantedCompletion.status == 3) {
                tableRow.setBackgroundColor(
                    ColourManager.selectColour(
                        habit.colour,
                        this
                    )
                )
            } else {
                tableRow.setBackgroundColor(Color.TRANSPARENT)
            }

        tableRow.tag = "$currentRow,$currentColumn"
        tableRow.setOnClickListener(editStatusListener)
        this.tableRow.addView(habitStatus)
        currentTableRow.addView(tableRow)
    }

    private fun initializeHabits(){
        rw = ReadWriteJson(filesDir.toString())
        habitList = rw.read()

        for (i in 0 until habitList.size){
            val thisCompletionList = habitList[i].completions
            if (thisCompletionList.find(0, thisCompletionList.completions.size, TIMESTAMP) == -1){
                val newCompletion = Completion(TIMESTAMP)
                habitList[i].completions.edit(newCompletion, 1)
            }
        }
    }
}
