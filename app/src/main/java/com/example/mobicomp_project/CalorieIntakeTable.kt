package com.example.mobicomp_project

import androidx.room.*

@Entity(tableName = "calorieIntakes")

data class CaloricIntake(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "timestamp") var timestamp: String?,
    @ColumnInfo(name = "calories") var calories: Int?,
    @ColumnInfo(name = "foodName") var foodName: String
)

@Dao
interface CalorieDao{
    @Transaction @Insert
    fun insert(caloricIntake: CaloricIntake)

    @Query("SELECT * FROM calorieIntakes")
    fun getCaloricIntakes(): List<CaloricIntake>

    @Query("SELECT * FROM calorieIntakes WHERE uid = :id")
    fun findById(id:Int): CaloricIntake

    @Update
    fun updateData(CaloricIntakeObject : CaloricIntake)

    @Query("DELETE FROM calorieIntakes WHERE uid = :id")
    fun delete(id:Int)

    @Query("DELETE FROM calorieIntakes")
    fun deleteAll()

}

@Entity(tableName = "dailyCalories")

data class DailyCalorieIntake(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "date") var date: Long?,
    @ColumnInfo(name = "dailyCalories") var dailyCalories: Int?
)

@Dao
interface DailyCalorieDao{
    @Transaction @Insert
    fun insert(dailyCalorieIntake: DailyCalorieIntake)

    @Query("SELECT * FROM dailyCalories")
    fun getDailyCalories(): List<DailyCalorieIntake>

    @Query("DELETE FROM dailyCalories WHERE uid = :id")
    fun delete(id:Int)

}