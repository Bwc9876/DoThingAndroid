package com.example.dothingandroid


import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

    public val context: TaskList = this@TaskList

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
            "bwc9876",
            group,
            "-x\$phI5|HO\$^4Y7<b(oywv8Jyo2IiyempboFmRi.z(Ouz-BNrmg7R(]hnMr|.4?^.Kf@kOwPY8<&3g_|_S&X2)v^%WL>i[4)r)>Ap?O=CCkTsYR(YCkf4Of:.\$1|q=+.II33Wte?>_9.yE%|v)jB|elTRc{{^qWMF)uidHSK5<rwng8Pq]Wj{AtL0hg?2DwX@rOW&K42k2sw!ZV#G&FNo6R0hy#0ur<}xMgkm+k)L|VVmFKZ^cmgrE#rJ7u:Wv1Q",
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
                    "bwc9876",
                    group.Name,
                    "-x\$phI5|HO\$^4Y7<b(oywv8Jyo2IiyempboFmRi.z(Ouz-BNrmg7R(]hnMr|.4?^.Kf@kOwPY8<&3g_|_S&X2)v^%WL>i[4)r)>Ap?O=CCkTsYR(YCkf4Of:.\$1|q=+.II33Wte?>_9.yE%|v)jB|elTRc{{^qWMF)uidHSK5<rwng8Pq]Wj{AtL0hg?2DwX@rOW&K42k2sw!ZV#G&FNo6R0hy#0ur<}xMgkm+k)L|VVmFKZ^cmgrE#rJ7u:Wv1Q",
                    groupViewModel
                )
            }
        } else if (requestCode == TaskAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(TaskAddActivity.EXTRA_REPLY)?.let {
                //TODO: Add ID Generating System
                val task = Task(123456789, it.split("/")[0], false)
                DBManager().AddTask(
                    "192.168.86.29",
                    8080,
                    "bwc9876",
                    it.split("/")[1],
                    "-x\$phI5|HO\$^4Y7<b(oywv8Jyo2IiyempboFmRi.z(Ouz-BNrmg7R(]hnMr|.4?^.Kf@kOwPY8<&3g_|_S&X2)v^%WL>i[4)r)>Ap?O=CCkTsYR(YCkf4Of:.\$1|q=+.II33Wte?>_9.yE%|v)jB|elTRc{{^qWMF)uidHSK5<rwng8Pq]Wj{AtL0hg?2DwX@rOW&K42k2sw!ZV#G&FNo6R0hy#0ur<}xMgkm+k)L|VVmFKZ^cmgrE#rJ7u:Wv1Q",
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

    companion object {
        const val EXTRA_REPLY = "com.example.dothingandroid.REPLY"
    }

}