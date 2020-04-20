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
    }

    override fun onResume() {
        super.onResume()
        refreshGraph()
        refreshCalorieText()
    }

    private fun refreshCalorieText(){
        var dailyCaloriesTotal = 0
        val formatter = DateTimeFormatter.ofPattern("d/M/y H:mm")

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
            val caloricIntakes = db.calorieDao().getCaloricIntakes()

            for (clrIntake in caloricIntakes){
                val datetime = LocalDateTime.parse(clrIntake.timestamp, formatter)
                if (datetime.toLocalDate() == LocalDate.now()){
                    dailyCaloriesTotal = dailyCaloriesTotal + clrIntake.calories
                }
            }
            println(dailyCaloriesTotal)
            uiThread { calories_text.setText(dailyCaloriesTotal.toString()) }
        }





    }
    private fun refreshGraph() {

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()

            Log.e("dbdebug", "db buildattu refreshgraph")

            val dailycalories = db.dailyCalorieDao().getWeekData()

            for (dailycalory in dailycalories) {
                println(dailycalory.uid)
                //println(dailycalory.dailyCalories)
            }

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
