package com.example.dothingandroid

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = arrayOf(Group::class, User::class), version = 2, exportSchema = false)
abstract class UserData : RoomDatabase() {

    abstract fun GroupAccess(): GroupAccess
    abstract fun UserAccess(): UserAccess

    companion object {
        @Volatile
        private var INSTANCE: UserData? = null

        fun getDatabase(context: Context, scope: CoroutineScope): UserData {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("CREATE TABLE 'UserTable' ('Name' TEXT NOT NULL, 'Token' TEXT NOT NULL, PRIMARY KEY('Name'))")
                }
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserData::class.java,
                    "UserData"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}