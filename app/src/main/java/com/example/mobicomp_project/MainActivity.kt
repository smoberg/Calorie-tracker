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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_intake.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.nio.channels.NonReadableChannelException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

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
    /*
    private fun makeDBEntries(){
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
            val formatter = DateTimeFormatter.ofPattern("d/M/y")
            var i=0

            while (i<5){
                val dailyCalorieTotal = DailyCalorieIntake(
                    uid = null,
                    date = LocalDate.of(2020,4,16).plusDays(i.toLong()).format(formatter),
                    dailyCalories = Random.nextInt(1500,2500)
                )
                db.dailyCalorieDao().insert(dailyCalorieTotal)
                i+=1
            }
            db.close()

        }
    }

     */



    override fun onResume() {
        super.onResume()
        //makeDBEntries()
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
            Log.e("dbdebug", "calories of the day queried from db %d".format(dailyCaloriesTotal))

            if (dailyClrTotal != null){
                dailyClrTotal.dailyCalories = dailyCaloriesTotal
                db.dailyCalorieDao().updateData(dailyClrTotal)
                Log.e("dbdebug", "Updated daily calories total %d".format(dailyCaloriesTotal))
                db.close()
            }
            else{
                db.close()
            }

            uiThread {
                calories_text.setText(dailyCaloriesTotal.toString())
                refreshGraph()
            }
        }
    }


    private fun refreshGraph() {
        // Refreshes bar graph with the recent data. Queries database for data from last seven days and updates them to the bar graph

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()

            Log.e("dbdebug", "db buildattu refreshgraph")

            val dailyCalories = db.dailyCalorieDao().getWeekData()

            Log.e("dbdebug", "size of weekdata %d".format(dailyCalories.size))

            db.close()

            uiThread {

                for (asd in dailyCalories){
                    println("%d %s".format(asd.dailyCalories, asd.date))
                }

                val entries = mutableListOf<BarEntry>()
                val labels = mutableListOf<String>()
                var i = 0

                for (dayData in dailyCalories){
                    entries.add(BarEntry(i.toFloat(),dayData.dailyCalories.toFloat()))
                    labels.add(LocalDate.parse(dayData.date, DateTimeFormatter.ofPattern("d/M/y")).format(
                        DateTimeFormatter.ofPattern("d/M")).toString())
                    i+=1
                }

                val set = BarDataSet(entries, "Caloric intake of previous seven days")
                set.color = Color.parseColor("#009688")

                val data = BarData(set)
                data.barWidth=0.9f

                val xAxis = calorie_chart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)

                calorie_chart.data=data
                calorie_chart.invalidate()
                calorie_chart.setFitBars(true)
                calorie_chart.description.isEnabled=false

            }
        }


    }

}
