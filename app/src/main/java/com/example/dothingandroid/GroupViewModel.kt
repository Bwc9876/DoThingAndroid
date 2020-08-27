package com.example.dothingandroid

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application){

    private val repository: UserDataRepo

    val GroupAccess = UserData.getDatabase(application, viewModelScope).GroupAccess()

    val allGroups: LiveData<List<Group>>

    init {
        repository = UserDataRepo(GroupAccess)
        allGroups = repository.allGroups

    }

    fun insert(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(group)
    }

    fun GetGroupDAO(): GroupAccess{
        return GroupAccess
    }

    fun GetHighestId(): Int{
        Log.d("DEBUG", GroupAccess.GetHighestPos().toString())
        return GroupAccess.GetHighestPos()
    }

}