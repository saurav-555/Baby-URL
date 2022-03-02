package com.example.android.babyurl.database

import android.content.Context
import android.os.strictmode.InstanceCountViolation
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.gms.dynamic.IFragmentWrapper


@Database(entities = [UrlEntity::class], version = 1, exportSchema = false)
abstract class UrlDatabase : RoomDatabase() {

    abstract val urlDao: UrlDao

    companion object {

        @Volatile
        private var INSTANCE: UrlDatabase? = null

        fun getInstance(context: Context): UrlDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UrlDatabase::class.java,
                        "UrlDatabase"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance

            }
        }
    }

}