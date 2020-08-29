package com.example.dothingandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    lateinit var groupViewModel: GroupViewModel

    private val GroupAddActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //For now, this will delete all users to make sure that I can test the login/register system
        setContentView(R.layout.activity_main)
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        DBManager().Continue_if_Data(groupViewModel, this)
    }
}
