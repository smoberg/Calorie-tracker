package com.example.mobicomp_project

import androidx.room.*

@Entity(tableName = "calorieIntakes")

data class CaloricIntake(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "timestamp") var timestamp: String?,
    @ColumnInfo(name = "calories") var calories: Int,
    @ColumnInfo(name = "foodName") var foodName: String
)

@Dao
interface CalorieDao{
    @Transaction @Insert
    fun insert(caloricIntake: CaloricIntake)

    @Query("SELECT * FROM calorieIntakes")
    fun getCaloricIntakes(): List<CaloricIntake>

    @Query("SELECT * FROM calorieIntakes WHERE uid = :id")
    fun findById(id:Int?): CaloricIntake

    @Update
    fun updateData(CaloricIntakeObject : CaloricIntake)

    @Query("DELETE FROM calorieIntakes WHERE uid = :id")
    fun delete(id:Int?)

    @Query("DELETE FROM calorieIntakes")
    fun deleteAll()

}

@Entity(tableName = "dailyCalories")


data class DailyCalorieIntake(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "dailyCalories") var dailyCalories: Int
)

@Dao
interface DailyCalorieDao{
    @Transaction @Insert
    fun insert(dailyCalorieIntake: DailyCalorieIntake)

    @Query("SELECT * FROM dailyCalories")
    fun getDailyCalories(): List<DailyCalorieIntake>

    @Query("SELECT * FROM dailyCalories WHERE date = :date")
    fun findByDate(date:String?): DailyCalorieIntake?

    @Query("SELECT * FROM (SELECT * FROM dailyCalories ORDER BY uid DESC LIMIT 7) ORDER BY uid ASC")
    fun getWeekData(): List<DailyCalorieIntake>

    @Update
    fun updateData(dailyCalorieIntake: DailyCalorieIntake)

    @Query("DELETE FROM dailyCalories WHERE uid = :id")
    fun delete(id:Int)

    @Query("DELETE FROM dailyCalories")
    fun deleteAll()

}