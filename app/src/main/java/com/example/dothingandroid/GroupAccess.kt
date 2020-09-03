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

    @Query("SELECT * from GroupTable WHERE Name = :groupname")
    fun GetRawGroupByName(groupname: String): Group

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(group: Group)

    @Query("DELETE FROM GroupTable")
    suspend fun deleteAll()

    @Query("SELECT MAX(Position) FROM GroupTable")
    fun GetHighestPos(): Int

    @Query("DELETE FROM GroupTable WHERE Name = :groupname")
    fun RemoveGroup(groupname: String)

    @Query("UPDATE GroupTable SET Position = :newpos WHERE Name = :groupname")
    fun UpdatePos(groupname: String, newpos: Int)

    @Query("UPDATE GroupTable SET Items = :newitems WHERE Name = :groupname")
    fun UpdateItems(groupname: String, newitems: String)

    @Query("UPDATE GroupTable SET Name = :newname WHERE Name = :oldname")
    fun UpdateName(newname: String, oldname: String)

}