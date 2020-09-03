package com.example.dothingandroid

import android.app.Activity
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.ConnectException
import kotlin.random.Random

class DBManager(loc: Boolean) {


    var local: Boolean = true
    private val default_ip: String = "71.225.44.187"
    private val default_local_ip: String = "192.168.86.29"
    private val default_auth_ip: String = "192.168.86.39"

    init {
        local = loc
    }

    fun GetIP(needed: String): String {
        if (local) {
            if (needed == "MAIN") {
                return default_local_ip
            } else {
                return default_auth_ip
            }
        } else {
            return default_ip
        }
    }


    fun DeleteGroup(viewDB: GroupViewModel, name: String) {
        GlobalScope.launch {
            viewDB.GetGroupDAO().RemoveGroup(name)
            val user = viewDB.GetUserDAO().GetCurrentUser()[0]
            val con = Connection(GetIP("MAIN"), 8080)
            con.send("D/${user.Name}/$name/${user.Token}/JAVA")
            val returned: String? = con.recv()
            if (returned == "IT") {
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                Log.e("ERROR", "Invalid User")
            }
            con.send("Ready")
            con.dc()
        }
    }


    fun RenameGroup(viewDB: GroupViewModel, newname: String, oldname: String) {
        GlobalScope.launch {
            val user = viewDB.GetUserDAO().GetCurrentUser()[0]
            viewDB.GetGroupDAO().UpdateName(newname, oldname)
            val con = Connection(GetIP("MAIN"), 8080)
            con.send("N/${user.Name}/$oldname/${user.Token}/JAVA")
            val returned: String? = con.recv()
            if (returned == "IT") {
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                Log.e("ERROR", "Invalid User")
            }
            con.send("Ready")
            con.WaitUntilRecv()
            con.send(newname)
            con.dc()
        }
    }

    fun Continue_if_Data(viewDB: GroupViewModel, mView: Activity) {
        GlobalScope.launch {
            val users = viewDB.GetUserDAO().GetCurrentUser()
            if (users.size > 0) {
                Log.d("DEBUG", "User detected")
                viewDB.GetGroupDAO().deleteAll()
                PopulateDB(viewDB)
                val intent = Intent(mView, TaskList::class.java)
                mView.startActivity(intent)
            }
            Log.d("DEBUG", "HA HA HA HA ONE")
        }
    }

    fun Logout(viewDB: GroupViewModel, mAct: Activity) {
        GlobalScope.launch {
            viewDB.GetUserDAO().deleteAll()
            val intent = Intent(mAct, MainActivity::class.java)
            mAct.startActivity(intent)
        }
    }

    fun Refresh(viewDB: GroupViewModel) {
        GlobalScope.launch {
            viewDB.GetGroupDAO().deleteAll()
            PopulateDB(viewDB)
        }

    }

    fun PopulateDB(
        viewDB: GroupViewModel,
    ) {
        GlobalScope.launch {
            val user = viewDB.GetUserDAO().GetCurrentUser()[0]
            val username = user.Name
            val token = user.Token
            val out: List<String?> = NON_SAFE_GetGroups(username, token)
            for (task in out) {
                task?.let {
                    Log.d("DEBUG", it)
                    var items: MutableList<String> = ArrayList()
                    try {
                        items = NON_SAFE_GetTasks(username, it, token)
                    } catch (conerror: ConnectException) {
                        //TODO: SHOW THIS TO USER
                        Log.e("NETWORK", "Connection refused")
                        this.cancel("Network Error")
                    }
                    Log.i("Info", "Items: " + items.joinToString("/"))
                    val position = items.removeFirst().toInt()
                    val itemstring = items.joinToString(separator = "/")
                    viewDB.GetGroupDAO().insert(Group(it, position, itemstring))
                }
            }
        }
    }

    fun NON_SAFE_GetGroups(
        username: String,
        token: String,
    ): List<String?> {
        val con = Connection(GetIP("MAIN"), 8080)
        con.send("G/$username/NONE/$token/JAVA")
        val returned: String = con.recv()
        if (returned == "IT") {
            Log.e("ERROR", "Invalid Token")
        } else if (returned == "IU") {
            Log.e("ERROR", "Invalid User")
        }
        con.send("Ready")
        val out: List<String?> = con.RecvList("GO", "END")
        Log.i("INFO", out.joinToString("/"))
        con.dc()
        return out
    }

    fun rand(start: Int, end: Int): Int {
        require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(end - start + 1) + start
    }


    fun NON_SAFE_GetTasks(
        username: String,
        group: String,
        token: String
    ): MutableList<String> {
        val con = Connection(GetIP("MAIN"), 8080)
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

    fun NON_SAFE_GenIdForTask(
        username: String,
        token: String,
    ): Int {
        val out: List<String?> = NON_SAFE_GetGroups(username, token)
        val taken: MutableList<Int> = ArrayList()
        for (group in out) {
            group?.let {
                val items = NON_SAFE_GetTasks(username, it, token)
                items.removeFirst()
                for (i in items) {
                    if (i != "NONE") {
                        taken.add(i.split(",")[0].toInt())
                    }
                }
            }
        }

        var newid: Int

        while (true) {
            newid = rand(0, 10000)
            if (!taken.contains(newid)) {
                break
            }
        }

        return newid

    }

    fun AddGroup(
        group: String,
        viewDB: GroupViewModel
    ) {
        GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val highest = viewDB.GetHighestId()
            val newpos: Int
            if (highest == -1) {
                newpos = 0
            } else {
                newpos = highest + 1
            }
            viewDB.GetGroupDAO().UpdatePos(group, newpos)
            NON_SAFE_PushGroup(username, group, token, viewDB)
        }
    }

    fun NON_SAFE_PushGroup(
        username: String,
        group: String,
        token: String,
        viewDB: GroupViewModel
    ) {
        val con = Connection(GetIP("MAIN"), 8080)
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
        group: String,
        viewDB: GroupViewModel,
        newtask: Task
    ) {
        GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val g = viewDB.GetGroupDAO().GetRawGroupByName(group)
            var items = g.Items
            newtask.id = NON_SAFE_GenIdForTask(user.Name, user.Token)
            if (items == "NONE") {
                items = newtask.ConSelfToString()
            } else {
                items = items + "/" + newtask.ConSelfToString()
            }
            viewDB.GetGroupDAO().UpdateItems(group, items)
            NON_SAFE_PushGroup(user.Name, group, user.Token, viewDB)
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

    fun ConstructTaskList(taskstring: String): MutableList<Task> {
        val out: MutableList<Task> = ArrayList()
        val taskrawstringlist = taskstring.split("/")
        for (taskstringraw in taskrawstringlist) {
            if (taskstringraw != "NONE") {
                val taskvalues = taskstringraw.split(",")
                out.add(
                    Task(
                        RemoveNonDigits(taskvalues[0]).toInt(),
                        taskvalues[1],
                        taskvalues[2].toBoolean()
                    )
                )
            }
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


    fun DeleteTask(
        group: String,
        viewDB: GroupViewModel,
        taskname: String,
    ) {
        GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items.removeAt(items.indexOf(FindItemFromListById(taskname.toInt(), items)))
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token, viewDB)
        }
    }


    fun EditTaskName(
        group: String,
        viewDB: GroupViewModel,
        taskname: String,
        newname: String
    ) {
        GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items[items.indexOf(FindItemFromListById(taskname.toInt(), items))].name = newname
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token, viewDB)
        }
    }


    fun ToggleTask(
        group: String,
        viewDB: GroupViewModel,
        taskname: String,
        done: Boolean
    ) {
        GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items[items.indexOf(FindItemFromListById(taskname.toInt(), items))].done = done
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token, viewDB)
        }
    }


    fun NON_SAFE_Get_User_Data(viewDB: GroupViewModel): User {
        return viewDB.GetUserDAO().GetCurrentUser()[0]
    }

    fun Login(username: String, password: String, viewDB: GroupViewModel, act: Activity) {
        GlobalScope.launch {
            val con = Connection(GetIP("AUTH"), 8081)
            con.send("L/$username/$password/JAVA")
            val returned = con.WaitUntilRecv()
            con.dc()
            //TODO: Show error messages to user
            if (returned == "IL") {
                Log.e("AUTH", "Invalid Login")
            } else if (returned == "IE") {
                Log.e("AUTH", "Internal Server Error")
            } else {
                val userdata = returned.split("/")
                val UserDAO = viewDB.GetUserDAO()
                UserDAO.deleteAll()
                UserDAO.insert(User(userdata[0], userdata[1]))
                Continue_if_Data(viewDB, act)
            }
        }
    }

    fun NON_SAFE_Add_User(name: String, token: String) {
        val con = Connection(GetIP("MAIN"), 8080)
        con.send("U/$name/NONE/$token/JAVA")
        con.WaitUntilRecv()
        con.send("Ready")
        con.dc()
    }

    fun Register(username: String, password: String, viewDB: GroupViewModel, act: Activity) {
        GlobalScope.launch {
            val con = Connection(GetIP("AUTH"), 8081)
            con.send("R/$username/$password/JAVA")
            val returned = con.WaitUntilRecv()
            con.dc()
            //TODO: Show error messages to user
            if (returned == "UE") {
                Log.e("AUTH", "User Exists")
            } else if (returned == "IE") {
                Log.e("AUTH", "Internal Server Error")
            } else {
                val userdata = returned.split("/")
                NON_SAFE_Add_User(userdata[0], userdata[1])
                val UserDAO = viewDB.GetUserDAO()
                UserDAO.deleteAll()
                UserDAO.insert(User(userdata[0], userdata[1]))
                Continue_if_Data(viewDB, act)
            }
        }
    }


}