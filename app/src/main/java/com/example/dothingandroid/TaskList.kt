package com.example.dothingandroid

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.cachapa.expandablelayout.ExpandableLayout
import org.w3c.dom.Attr

class TaskList : AppCompatActivity() {

    private lateinit var groupViewModel: GroupViewModel

    private val GroupAddActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = GroupListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this, Observer { groups ->
            groups?.let { adapter.setGroups(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@TaskList, GroupAddActivity::class.java)
            startActivityForResult(intent, GroupAddActivityRequestCode)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GroupAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(GroupAddActivity.EXTRA_REPLY)?.let{
                //TODO: Add A Get From The Database To Get Highest Position And Add One
                val group = Group(it, 1234, "")
                groupViewModel.insert(group)
            }
        }
        else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }


    }
}