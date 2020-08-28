package com.example.dothingandroid

import androidx.lifecycle.LiveData

class UserDataRepo(private val GroupDAO: GroupAccess) {


    val allGroups: LiveData<List<Group>> = GroupDAO.GetSortedGroups()

    suspend fun insert(group: Group) {
        GroupDAO.insert(group)
    }

}