package com.example.dothingandroid

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Group::class), version = 1, exportSchema = false)
public abstract class UserData : RoomDatabase() {

    abstract fun GroupAccess(): GroupAccess

    private class UserDataCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                database -> scope.launch{populateDatabase(database.GroupAccess())}
            }
        }

        suspend fun populateDatabase(groupAccess: GroupAccess){
            groupAccess.deleteAll()
            DBManager().PopulateDB("192.168.86.29", 8080, "bwc9876", "NONE", "-x\$phI5|HO\$^4Y7<b(oywv8Jyo2IiyempboFmRi.z(Ouz-BNrmg7R(]hnMr|.4?^.Kf@kOwPY8<&3g_|_S&X2)v^%WL>i[4)r)>Ap?O=CCkTsYR(YCkf4Of:.\$1|q=+.II33Wte?>_9.yE%|v)jB|elTRc{{^qWMF)uidHSK5<rwng8Pq]Wj{AtL0hg?2DwX@rOW&K42k2sw!ZV#G&FNo6R0hy#0ur<}xMgkm+k)L|VVmFKZ^cmgrE#rJ7u:Wv1Q", groupAccess)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserData? = null

        fun getDatabase(context: Context, scope: CoroutineScope): UserData {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, UserData::class.java, "UserData").addCallback(UserDataCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}