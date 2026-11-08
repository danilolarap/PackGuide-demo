package com.example.packguide.data.auth

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserAccount::class], version = 1, exportSchema = false)
abstract class PackGuideDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var instance: PackGuideDatabase? = null

        fun getInstance(context: Context): PackGuideDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                PackGuideDatabase::class.java,
                "packguide.db"
            ).build().also { instance = it }
        }
    }
}
