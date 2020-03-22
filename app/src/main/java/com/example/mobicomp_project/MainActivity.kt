package com.example.mobicomp_project

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.graphics.blue
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calories_text.setText("1234")


        fab.setOnClickListener{
            startActivity(Intent(applicationContext, IntakeActivity::class.java))

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
