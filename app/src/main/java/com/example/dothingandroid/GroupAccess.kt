package com.example.dothingandroid

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupAccess {
    @Query("SELECT * from GroupTable ORDER BY Position ASC")
    fun GetSortedGroups(): LiveData<List<Group>>

    @Query("SELECT * from GroupTable WHERE Name = :groupname")
    fun GetGroupByName(groupname: String): LiveData<Group>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(group: Group)

    @Query("DELETE FROM GroupTable")
    suspend fun deleteAll()

    @Query("SELECT MAX(Position) FROM GroupTable")
    fun GetHighestPos(): LiveData<Int>

}