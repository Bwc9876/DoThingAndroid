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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TaskList : AppCompatActivity() {

    private lateinit var groupViewModel: GroupViewModel

    private val GroupAddActivityRequestCode = 1

    val context: TaskList = this@TaskList

    private val TaskAddActivityRequestCode = 2

    private val TaskEditActivityRequestCode = 4

    private val GroupEditActivityRequestCode = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = GroupListAdapter(this@TaskList)
        adapter.GroupListAdapter(context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@TaskList)
        groupViewModel = ViewModelProvider(this@TaskList).get(GroupViewModel::class.java)
        groupViewModel.allGroups.observe(this@TaskList, Observer { groups ->
            groups?.let { adapter.setGroups(it) }
        })

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            val intent = Intent(this@TaskList, GroupAddActivity::class.java)
            startActivityForResult(intent, GroupAddActivityRequestCode)
        }


    }

    fun MoveGroup(group: String, newindex: Int) {
        GlobalScope.launch {
            val db = DBManager(
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getBoolean("diffIP", false),
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getString("customIP", "None") as String, groupViewModel, this@TaskList
            )
            if (db.valid) {
                db.MoveGroup(newindex, group)
            }
        }
    }

    fun ChangeTaskOrder(task: String, newindex: Int, group: String, newgroup: String) {
        GlobalScope.launch {
            val db = DBManager(
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getBoolean("diffIP", false),
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getString("customIP", "None") as String, groupViewModel, this@TaskList
            )
            if (db.valid) {
                db.ChangeTaskOrder(group, task, newindex, newgroup)
            }
        }
    }

    fun MoveTask(group: String, newgroup: String, task: String) {
        GlobalScope.launch {
            val db = DBManager(
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getBoolean("diffIP", false),
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getString("customIP", "None") as String, groupViewModel, this@TaskList
            )
            if (db.valid) {
                db.MoveTask(group, newgroup, task)
            }
        }
    }

    fun ToggleDone(group: String, task: String, done: Boolean) {
        GlobalScope.launch {
            val db = DBManager(
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getBoolean("diffIP", false),
                PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                    .getString("customIP", "None") as String, groupViewModel, this@TaskList
            )
            if (db.valid) {
                db.ToggleTask(
                    group,
                    task,
                    done
                )
            }
        }
    }

    fun StartTaskAdd(group: String) {
        val intent = Intent(this@TaskList, TaskAddActivity::class.java)
        intent.putExtra(TaskAddActivity.EXTRA_REPLY, group)
        startActivityForResult(intent, TaskAddActivityRequestCode)
    }

    fun StartTaskEdit(group: String, task: String) {
        val intent = Intent(this@TaskList, TaskEditActivity::class.java)
        intent.putExtra(EditGroupActivity.EXTRA_REPLY, "$group/$task")
        startActivityForResult(intent, TaskEditActivityRequestCode)
    }

    fun StartGroupEdit(group: String) {
        val intent = Intent(this@TaskList, EditGroupActivity::class.java)
        intent.putExtra(EditGroupActivity.EXTRA_REPLY, group)
        startActivityForResult(intent, GroupEditActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GroupAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(GroupAddActivity.EXTRA_REPLY)?.let {
                val group = Group(it, -1, "NONE")
                groupViewModel.insert(group)
                GlobalScope.launch {
                    val db = DBManager(
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getBoolean("diffIP", false),
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getString("customIP", "None") as String, groupViewModel, this@TaskList
                    )
                    if (db.valid) {
                        db.AddGroup(
                            group.Name
                        )
                    }
                }
            }
        } else if (requestCode == TaskAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(TaskAddActivity.EXTRA_REPLY)?.let {
                val task = Task(-1, it.split("/")[0], false)
                GlobalScope.launch {
                    val db = DBManager(
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getBoolean("diffIP", false),
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getString("customIP", "None") as String, groupViewModel, this@TaskList
                    )
                    if (db.valid) {
                        db.AddTask(it.split("/")[1], task)
                    }
                }
            }
        } else if (requestCode == GroupEditActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(TaskAddActivity.EXTRA_REPLY)?.let {
                val new = it.split('/')[0]
                val old = it.split('/')[1]
                if (new == "**DEL**") {
                    GlobalScope.launch {
                        val db = DBManager(
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getBoolean("diffIP", false),
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getString("customIP", "None") as String,
                            groupViewModel,
                            this@TaskList
                        )
                        if (db.valid) {
                            db.DeleteGroup(old)
                        }
                    }

                } else {
                    GlobalScope.launch {
                        val db = DBManager(
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getBoolean("diffIP", false),
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getString("customIP", "None") as String,
                            groupViewModel,
                            this@TaskList
                        )
                        if (db.valid) {
                            db.RenameGroup(new, old)
                        }
                    }
                }
            }
        } else if (requestCode == TaskEditActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(TaskEditActivity.EXTRA_REPLY)?.let {
                val group = it.split("/")[0]
                val new = it.split('/')[1]
                val old = it.split('/')[2]
                if (new == "**DEL**") {
                    GlobalScope.launch {
                        val db = DBManager(
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getBoolean("diffIP", false),
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getString("customIP", "None") as String,
                            groupViewModel,
                            this@TaskList
                        )
                        if (db.valid) {
                            db.DeleteTask(group, old)
                        }
                    }
                } else {
                    GlobalScope.launch {
                        val db = DBManager(
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getBoolean("diffIP", false),
                            PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                                .getString("customIP", "None") as String,
                            groupViewModel,
                            this@TaskList
                        )
                        if (db.valid) {
                            db.EditTaskName(group, old, new)
                        }
                    }
                }

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
        // Inflate the menu; this@TaskList adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                GlobalScope.launch {
                    val db = DBManager(
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getBoolean("diffIP", false),
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getString("customIP", "None") as String,
                        groupViewModel,
                        this@TaskList,
                        bypassCheck = true
                    )
                    if (db.valid) {
                        db.Logout()
                    }
                }
                true
            }
            R.id.action_refresh -> {
                GlobalScope.launch {
                    val db = DBManager(
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getBoolean("diffIP", false),
                        PreferenceManager.getDefaultSharedPreferences(this@TaskList)
                            .getString("customIP", "None") as String, groupViewModel, this@TaskList
                    )
                    if (db.valid) {
                        db.Refresh()
                    }
                }
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this@TaskList, SettingsActivity::class.java)
                startActivity(intent)
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