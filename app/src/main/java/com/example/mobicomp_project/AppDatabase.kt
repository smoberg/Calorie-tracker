package com.example.mobicomp_project

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CaloricIntake::class, DailyCalorieIntake::class],  version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calorieDao() : CalorieDao
    abstract fun dailyCalorieDao(): DailyCalorieDao
}