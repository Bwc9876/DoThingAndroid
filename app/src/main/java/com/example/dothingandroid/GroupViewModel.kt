package com.example.dothingandroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application){

    private val repository: UserDataRepo

    val allGroups: LiveData<List<Group>>

    init {
        val GroupAccess = UserData.getDatabase(application, viewModelScope).GroupAccess()
        repository = UserDataRepo(GroupAccess)
        allGroups = repository.allGroups

    }

    fun insert(group: Group) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(group)
    }
}