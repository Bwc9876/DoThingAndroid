package com.example.dothingandroid



import androidx.lifecycle.viewModelScope
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

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        val fab1 = findViewById<View>(R.id.fab1) as FloatingActionButton
        val fab2 = findViewById<View>(R.id.fab2) as FloatingActionButton
        fab.setOnClickListener{
                if (!isFABOpen) {
                    showFABMenu(fab1, fab2)
                } else {
                    closeFABMenu(fab1, fab2)
                }
        }
        closeFABMenu(fab1, fab2)

        fab1.setOnClickListener {
            val intent = Intent(this@TaskList, GroupAddActivity::class.java)
            startActivityForResult(intent, GroupAddActivityRequestCode)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GroupAddActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(GroupAddActivity.EXTRA_REPLY)?.let{
                val group = Group(it, -1, "NONE")
                groupViewModel.insert(group)
                DBManager().PushGroup("192.168.86.29", 8080, "bwc9876", group.Name, "-x\$phI5|HO\$^4Y7<b(oywv8Jyo2IiyempboFmRi.z(Ouz-BNrmg7R(]hnMr|.4?^.Kf@kOwPY8<&3g_|_S&X2)v^%WL>i[4)r)>Ap?O=CCkTsYR(YCkf4Of:.\$1|q=+.II33Wte?>_9.yE%|v)jB|elTRc{{^qWMF)uidHSK5<rwng8Pq]Wj{AtL0hg?2DwX@rOW&K42k2sw!ZV#G&FNo6R0hy#0ur<}xMgkm+k)L|VVmFKZ^cmgrE#rJ7u:Wv1Q", groupViewModel)
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

    private fun showFABMenu(fab1: FloatingActionButton, fab2: FloatingActionButton) {
        isFABOpen = true
        fab1.animate().translationY(-resources.getDimension(R.dimen.GroupAddBias))
        fab2.animate().translationY(-resources.getDimension(R.dimen.TaskAddBias))
    }

    private fun closeFABMenu(fab1: FloatingActionButton, fab2: FloatingActionButton) {
        isFABOpen = false
        fab1.animate().translationY(0f)
        fab2.animate().translationY(0f)
    }

}