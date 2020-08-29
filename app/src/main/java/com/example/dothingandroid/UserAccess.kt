package com.example.dothingandroid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserAccess {

    @Query("SELECT Token FROM UserTable WHERE Name = :username")
    fun GetToken(username: String): String

    @Query("SELECT * FROM UserTable")
    fun GetCurrentUser(): List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("DELETE FROM UserTable")
    suspend fun deleteAll()

}