package com.example.dothingandroid


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TaskList : AppCompatActivity() {

    private lateinit var groupViewModel: GroupViewModel

    private var isFABOpen: Boolean = false

    private val GroupAddActivityRequestCode = 1

    val context: TaskList = this@TaskList

    private val TaskAddActivityRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = GroupListAdapter(this)
        adapter.GroupListAdapter(context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this, Observer { groups ->
            groups?.let { adapter.setGroups(it) }
        })

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            val intent = Intent(this@TaskList, GroupAddActivity::class.java)
            startActivityForResult(intent, GroupAddActivityRequestCode)
        }


    }

    fun ToggleDone(group: String, task: String, done: Boolean) {
        DBManager().ToggleTask(
            "192.168.86.29",
            8080,
            group,
            groupViewModel,
            task,
            done
        )
    }

    fun StartTaskAdd(group: String) {
        val intent = Intent(this@TaskList, TaskAddActivity::class.java)
        intent.putExtra(TaskAddActivity.EXTRA_REPLY, group)
        startActivityForResult(intent, TaskAddActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GroupAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(GroupAddActivity.EXTRA_REPLY)?.let {
                val group = Group(it, -1, "NONE")
                groupViewModel.insert(group)
                DBManager().AddGroup(
                    "192.168.86.29",
                    8080,
                    group.Name,
                    groupViewModel
                )
            }
        } else if (requestCode == TaskAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(TaskAddActivity.EXTRA_REPLY)?.let {
                //TODO: Add ID Generating System
                val task = Task(-1, it.split("/")[0], false)
                DBManager().AddTask(
                    "192.168.86.29",
                    8080,
                    it.split("/")[1],
                    groupViewModel,
                    task
                )
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                DBManager().Logout(groupViewModel, this)
                true
            }
            R.id.action_refresh -> {
                DBManager().Refresh(groupViewModel)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
                false
            }
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.dothingandroid.REPLY"
    }

}