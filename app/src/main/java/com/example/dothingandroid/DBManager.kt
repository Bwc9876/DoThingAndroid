package com.example.dothingandroid

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DBManager {

    fun PopulateDB(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String,
        groupDAO: GroupAccess
    ) {
        GlobalScope.launch {
            val con = Connection(ip, port)
            con.send("G/$username/$group/$token/JAVA")
            val returned: String? = con.recv()
            if (returned == "IT") {
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                Log.e("ERROR", "Invalid User")
            }
            con.send("Ready")
            val out: List<String?> = con.RecvList("GO", "END")
            con.dc()

            for (task in out) {
                task?.let {
                    Log.d("DEBUG", it)
                    val items = NON_SAFE_GetTasks(ip, port, username, it, token)
                    Log.i("INFO", items.joinToString(separator = "/"))
                    val position = items.removeFirst().toInt()
                    val itemstring = items.joinToString(separator = "/")
                    groupDAO.insert(Group(it, position, itemstring))
                }
            }

        }
    }

    fun NON_SAFE_GetTasks(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String
    ): MutableList<String> {
        val con = Connection(ip, port)
        con.send("R/$username/$group/$token/JAVA")
        val returned: String? = con.recv()
        if (returned == "IT") {
            Log.e("ERROR", "Invalid Token")
        } else if (returned == "IU") {
            Log.e("ERROR", "Invalid User")
        }
        con.send("Ready")
        val out: MutableList<String> = con.RecvList("GO", "END")
        con.dc()
        return out
    }

    fun AddGroup(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String,
        viewDB: GroupViewModel
    ) {
        GlobalScope.launch {
            val newpos = viewDB.GetHighestId() + 1
            viewDB.GetGroupDAO().UpdatePos(group, newpos)
            NON_SAFE_PushGroup(ip, port, username, group, token, viewDB)
        }
    }

    fun PushGroup(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String,
        viewDB: GroupViewModel
    ) {
        GlobalScope.launch {
            val con = Connection(ip, port)
            con.send("W/$username/$group/$token/JAVA")
            val returned: String? = con.recv()
            if (returned == "IT") {
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                Log.e("ERROR", "Invalid User")
            }
            con.send("Ready")
            val tasksraw = viewDB.GetGroupDAO().GetRawGroupByName(group).Items
            val pos = viewDB.GetGroupDAO().GetRawGroupByName(group).Position
            viewDB.GetGroupDAO().UpdatePos(group, pos)
            val tasks = (pos.toString() + "/" + tasksraw).split("/")
            con.SendList("END", tasks)
            con.dc()
        }
    }

    fun NON_SAFE_PushGroup(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String,
        viewDB: GroupViewModel
    ) {
        val con = Connection(ip, port)
        con.send("W/$username/$group/$token/JAVA")
        val returned: String? = con.recv()
        if (returned == "IT") {
            Log.e("ERROR", "Invalid Token")
        } else if (returned == "IU") {
            Log.e("ERROR", "Invalid User")
        }
        con.send("Ready")
        var tasksraw = viewDB.GetGroupDAO().GetRawGroupByName(group).Items
        val gpos = viewDB.GetGroupDAO().GetRawGroupByName(group).Position
        tasksraw = gpos.toString() + "/" + tasksraw
        Log.i("INFO", tasksraw)
        val tasks = tasksraw.split("/")
        con.SendList("END", tasks)
        con.dc()
    }

    fun AddTask(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String,
        viewDB: GroupViewModel,
        newtask: Task
    ) {
        GlobalScope.launch {
            val g = viewDB.GetGroupDAO().GetRawGroupByName(group)
            var items = g.Items
            items = items + "/" + newtask.ConSelfToString()
            viewDB.GetGroupDAO().UpdateItems(group, items)
            NON_SAFE_PushGroup(ip, port, username, group, token, viewDB)
        }
    }

    fun RemoveNonDigits(instr: String): String {
        var outstr = instr
        val out: MutableList<String> = ArrayList()
        out.add("0")
        out.add("1")
        out.add("2")
        out.add("3")
        out.add("4")
        out.add("5")
        out.add("6")
        out.add("7")
        out.add("8")
        out.add("9")
        for (ch in outstr) {
            if (!out.contains(ch.toString())) {
                outstr = outstr.replace(ch.toString(), "")
            }
        }
        return outstr
    }

    fun ConstructTask(taskstring: String): Task {
        val taskvalues = taskstring.split(",")
        return Task(
            RemoveNonDigits(taskvalues[0]).toInt(),
            taskvalues[1],
            taskvalues[2].toBoolean()
        )
    }

    fun ConstructTaskList(taskstring: String): MutableList<Task> {
        val out: MutableList<Task> = ArrayList()
        val taskrawstringlist = taskstring.split("/")
        for (taskstringraw in taskrawstringlist) {
            val taskvalues = taskstringraw.split(",")
            out.add(
                Task(
                    RemoveNonDigits(taskvalues[0]).toInt(),
                    taskvalues[1],
                    taskvalues[2].toBoolean()
                )
            )
        }
        return out
    }

    fun FindItemFromListById(id: Int, tasklist: MutableList<Task>): Task {
        for (i in tasklist) {
            if (i.id == id) {
                return i
            }
        }

        return Task(-1, "ERROR", false)

    }

    fun DeconstructTaskList(tasklist: MutableList<Task>): String {
        val taskrawlist: MutableList<String> = ArrayList()
        for (i in tasklist) {
            taskrawlist.add(i.ConSelfToString())
        }
        val out1 = taskrawlist.joinToString("/")
        return out1
    }

    fun NON_SAFE_GetTaskByID(
        group: String,
        viewDB: GroupViewModel,
        taskname: String
    ): MutableList<String> {
        val outlist: MutableList<String> = ArrayList()
        var out: Task = Task(-1, "ERROR", false)
        var pos: Int = -1
        val DAO = viewDB.GetGroupDAO()
        val groupobj = DAO.GetRawGroupByName(group)
        val tasks = ConstructTaskList(groupobj.Items)
        for (task in tasks) {
            if (task.id.toString() == taskname) {
                out = task
                pos = tasks.indexOf(task)
            }
        }
        outlist.add(out.ConSelfToString())
        outlist.add(pos.toString())
        return outlist

    }

    fun ToggleTask(
        ip: String,
        port: Int,
        username: String,
        group: String,
        token: String,
        viewDB: GroupViewModel,
        taskname: String,
        done: Boolean
    ) {
        GlobalScope.launch {
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items[items.indexOf(FindItemFromListById(taskname.toInt(), items))].done = done
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(ip, port, username, group, token, viewDB)
        }
    }

}