package com.vex.security.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ThreatEventEntity::class], version = 1, exportSchema = false)
abstract class VexDatabase : RoomDatabase() {

    abstract fun threatEventDao(): ThreatEventDao

    companion object {
        @Volatile
        private var INSTANCE: VexDatabase? = null

        fun getInstance(context: Context): VexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VexDatabase::class.java,
                    "vex_security.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
