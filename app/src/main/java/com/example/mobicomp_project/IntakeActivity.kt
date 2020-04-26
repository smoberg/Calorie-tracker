package com.example.mobicomp_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_intake.*
import kotlinx.android.synthetic.main.delete_window.view.*
import kotlinx.android.synthetic.main.dialog_window.*
import kotlinx.android.synthetic.main.dialog_window.view.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.time.format.DateTimeFormatter
import java.util.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month


class IntakeActivity : AppCompatActivity() {

    companion object {
        var selectedUid: Int? = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intake)


        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)


        btn_add.setOnClickListener {
            val addDialog = LayoutInflater.from(this).inflate(R.layout.dialog_window, null)
            val addBuilder = AlertDialog.Builder(this)
                .setView(addDialog)
                .setTitle("Add Item")

            val mAlertDialog = addBuilder.show()
            mAlertDialog.edit_time.setIs24HourView(true)

            addDialog.dialog_button_ok.setOnClickListener {

                val date = LocalDate.now()
                val time = LocalTime.of(mAlertDialog.edit_time.currentHour, mAlertDialog.edit_time.currentMinute)
                val datetime = LocalDateTime.of(date, time)

                val caloricIntake = CaloricIntake(
                    uid = null,
                    timestamp = datetime.format(DateTimeFormatter.ofPattern("d/M/y H:mm")),
                    calories = Integer.parseInt(mAlertDialog.edit_calories.text.toString()),
                    foodName = mAlertDialog.edit_food.text.toString()
                )

                Log.e("dbdebug", "db entry doned")

                // Adds new caloric intake to the database.
                doAsync {
                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
                    db.calorieDao().insert(caloricIntake)
                    db.close()
                }

                mAlertDialog.dismiss()
                refreshList()
            }

            addDialog.dialog_button_cancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        btn_delete.setOnClickListener {

            val deleteDialog = LayoutInflater.from(this).inflate(R.layout.delete_window, null)
            val deleteBuilder = AlertDialog.Builder(this)
                .setView(deleteDialog)
                .setTitle("Delete Item?")

            val mAlertDialog = deleteBuilder.show()

            deleteDialog.btn_delete_confirm.setOnClickListener {

                doAsync {


                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
                    val dId = selectedUid
                    db.calorieDao().delete(dId)
                    db.close()

                }
                mAlertDialog.dismiss()
                refreshList()
            }

            deleteDialog.btn_delete_cancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        btn_edit.setOnClickListener {

            val editDialog = LayoutInflater.from(this).inflate(R.layout.dialog_window, null)
            val editBuilder = AlertDialog.Builder(this)
                .setView(editDialog)
                .setTitle("Edit Item")

            val mAlertDialog = editBuilder.show()
            mAlertDialog.edit_time.setIs24HourView(true)

            editDialog.dialog_button_ok.setOnClickListener {

                // Queryy valitun objektin databasesta, edittaa siihen edittaus ikkunassa asetetut arvot.
                doAsync {

                    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()

                    val eId = selectedUid
                    val date = LocalDate.now()
                    val time = LocalTime.of(mAlertDialog.edit_time.currentHour, mAlertDialog.edit_time.currentMinute
                    )
                    val datetime = LocalDateTime.of(date, time)

                    val caloricIntake = db.calorieDao().findById(eId)
                    caloricIntake.calories = Integer.parseInt(mAlertDialog.edit_calories.text.toString())
                    caloricIntake.timestamp = datetime.format(DateTimeFormatter.ofPattern("d/M/y H:mm"))
                    caloricIntake.foodName = mAlertDialog.edit_food.text.toString()
                    db.calorieDao().updateData(caloricIntake)

                    db.close()

                }

                mAlertDialog.dismiss()
                refreshList()
            }

            editDialog.dialog_button_cancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }

    private fun refreshList() {

        var fabOpened = false

        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "calorieIntakes").build()
            val caloricIntakes = db.calorieDao().getCaloricIntakes()
            db.close()


            uiThread {

                if (caloricIntakes.isNotEmpty()) {
                    Log.e("dbdebug", "caloricintakes not empty")
                    Log.e("dbdebug", "size: %d".format(caloricIntakes.size))
                    Log.d("Caloric intakes listana", caloricIntakes.toString())

                    recycler_view.adapter = RecyclerviewAdapter(caloricIntakes) {

                        var selectedItem = it

                        selectedUid = selectedItem.uid

                        Log.e(
                            "Activity",
                            "Clicked on item %d, %s, %d, %s".format(
                                selectedItem.uid,
                                selectedItem.timestamp,
                                selectedItem.calories,
                                selectedItem.foodName
                            )
                        )

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


                } else {
                    Log.e("dbdebug", "tyhjä")
                }
            }

        }

    }
}