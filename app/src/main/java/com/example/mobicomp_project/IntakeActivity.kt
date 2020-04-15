package com.example.mobicomp_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_intake.*
import kotlinx.android.synthetic.main.delete_window.view.*
import kotlinx.android.synthetic.main.dialog_window.*
import kotlinx.android.synthetic.main.dialog_window.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.time.format.DateTimeFormatter


class IntakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intake)

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
            Log.e("dbdebug", "db buildattu intake avatessa")
            val caloricIntakes = db.calorieDao().getCaloricIntakes()

            db.close()
            uiThread {

                if(caloricIntakes.isNotEmpty()){
                    Log.e("dbdebug", "caloricintakes not empty")
                    Log.e("dbdebug", "size: %d".format(caloricIntakes.size))
                }
                else{
                    Log.e("dbdebug", "tyhjä")
                }
            }


        }




        btn_add.setOnClickListener{
            val addDialog = LayoutInflater.from(this).inflate(R.layout.dialog_window, null)
            val addBuilder = AlertDialog.Builder(this)
                .setView(addDialog)
                .setTitle("Add Item")

            val mAlertDialog = addBuilder.show()
            mAlertDialog.edit_time.setIs24HourView(true)

            addDialog.dialog_button_ok.setOnClickListener{

                var pickedTime = "%s:%s".format(mAlertDialog.edit_time.currentHour, mAlertDialog.edit_time.currentMinute)
                Log.e("dbdebug", "picked time: %s ".format(pickedTime))

                val caloricIntake = CaloricIntake(
                    uid = null,
                    timestamp = pickedTime,
                    calories = Integer.parseInt(mAlertDialog.edit_calories.text.toString()),
                    foodName = mAlertDialog.edit_food.text.toString()
                )

                Log.e("dbdebug", "db entry tehty")

                doAsync {
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
                    Log.e("dbdebug", "db buildattu insertissä")

                    db.calorieDao().insert(caloricIntake)
                    db.close()

                }

                mAlertDialog.dismiss()

            }

            addDialog.dialog_button_cancel.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }

        btn_delete.setOnClickListener{

            val deleteDialog = LayoutInflater.from(this).inflate(R.layout.delete_window, null)
            val deleteBuilder = AlertDialog.Builder(this)
                .setView(deleteDialog)
                .setTitle("Delete Item?")

            val mAlertDialog = deleteBuilder.show()

            deleteDialog.btn_delete_confirm.setOnClickListener{
                mAlertDialog.dismiss()
            }

            deleteDialog.btn_delete_cancel.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }

        btn_edit.setOnClickListener{

            val editDialog = LayoutInflater.from(this).inflate(R.layout.dialog_window, null)
            val editBuilder = AlertDialog.Builder(this)
                .setView(editDialog)
                .setTitle("Edit Item")

            val mAlertDialog = editBuilder.show()
            mAlertDialog.edit_time.setIs24HourView(true)

            editDialog.dialog_button_ok.setOnClickListener{
                mAlertDialog.dismiss()
            }

            editDialog.dialog_button_cancel.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }

        //val clickText = findViewById<TextView>(R.id.food_item)
        val clickText = food_item
        var fabOpened = false

        clickText.setOnClickListener{

            if(!fabOpened){

                fabOpened = true
                btn_edit.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                btn_delete.animate().translationY(-resources.getDimension(R.dimen.standard_116))

            } else {

                fabOpened = false
                btn_edit.animate().translationY(0f)
                btn_delete.animate().translationY(0f)
            }
        }



    }
}
