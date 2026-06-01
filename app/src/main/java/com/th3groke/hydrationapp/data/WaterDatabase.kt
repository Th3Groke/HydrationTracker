package com.th3groke.hydrationapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WaterLog::class], version=1, exportSchema = false)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun waterLogDao(): WaterLogDao

    companion object {
        @Volatile
        private var INSTANCE : WaterDatabase? =null
        fun getDatabase(context: Context): WaterDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, WaterDatabase::class.java,"water_database").fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}
