package com.example.mobicomp_project

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.graphics.blue
import androidx.room.Room
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_intake.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.nio.channels.NonReadableChannelException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fab.setOnClickListener{
            startActivity(Intent(applicationContext, IntakeActivity::class.java))

        }

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
            val formatter = DateTimeFormatter.ofPattern("d/M/y")
            val dailyCaloriesCurrent = db.dailyCalorieDao().findByDate(LocalDate.now().format(formatter))

            if (dailyCaloriesCurrent == null){
                //Makes empty object for the current day if there is not one already in database
                val dailyCalorieTotal = DailyCalorieIntake(
                    uid = null,
                    date = LocalDate.now().format(formatter),
                    dailyCalories = 0
                )
                db.dailyCalorieDao().insert(dailyCalorieTotal)
                db.close()
            }

        }


    }

    override fun onResume() {
        super.onResume()
        refreshGraph()
        refreshCalorieText()
    }

    private fun refreshCalorieText(){
        // Refreshes CalorieText shown at the top of the screen with calorie intake of current day.
        // Updates daily calorie total to database also.

        var dailyCaloriesTotal = 0
        val formatter = DateTimeFormatter.ofPattern("d/M/y H:mm")

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
            val caloricIntakes = db.calorieDao().getCaloricIntakes()

            for (clrIntake in caloricIntakes){
                val datetime = LocalDateTime.parse(clrIntake.timestamp, formatter)
                if (datetime.toLocalDate() == LocalDate.now()){
                    dailyCaloriesTotal += clrIntake.calories
                }
            }
            val dailyClrTotal = db.dailyCalorieDao().findByDate(LocalDate.now().format(DateTimeFormatter.ofPattern("d/M/y")))
                if (dailyClrTotal != null){
                    dailyClrTotal.dailyCalories = dailyCaloriesTotal
                    db.dailyCalorieDao().updateData(dailyClrTotal)
                    db.close()
                }
                else{
                    db.close()
                }

            uiThread { calories_text.setText(dailyCaloriesTotal.toString()) }
        }
    }


    private fun refreshGraph() {
        // Refreshes bar graph with the recent data. Queries database for data from last seven days and updates them to the bar graph
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()

            Log.e("dbdebug", "db buildattu refreshgraph")

            val dailyCalories = db.dailyCalorieDao().getWeekData()

            Log.e("dbdebug", "size of weekdata %d".format(dailyCalories.size))
            Log.e("dbdebug", "amount of daily calories %d".format(dailyCalories[0].dailyCalories))

            db.close()
        }


        //dummy data for the bar graph

        val entries = mutableListOf<BarEntry>()

        entries.add(BarEntry(0f, 2400f))
        entries.add(BarEntry(1f, 2200f))
        entries.add(BarEntry(2f, 1800f))
        entries.add(BarEntry(3f, 1500f))
        entries.add(BarEntry(4f, 2800f))
        entries.add(BarEntry(5f, 2900f))
        entries.add(BarEntry(6f, 2200f))

        val set = BarDataSet(entries, "Dummy data set")
        set.color = Color.parseColor("#009688")

        val data = BarData(set)
        data.barWidth=0.9f
        calorie_chart.data=data
        calorie_chart.setFitBars(true)
        calorie_chart.description.isEnabled=false



    }

}
