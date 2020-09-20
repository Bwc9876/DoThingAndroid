package com.example.dothingandroid

import android.app.Activity
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.ConnectException
import kotlin.random.Random

class DBManager(
    customIP: Boolean,
    serverIP: String,
    viewDB_IN: GroupViewModel,
    mView_IN: Activity,
    bypassCheck: Boolean = false
) {

    var custom: Boolean = false
    var bypass = false
    var userServerIP: String = "None"
    var viewDB: GroupViewModel
    var valid = true
    var mView: Activity
    lateinit var job: Job
    private val default_ip: String = "71.225.44.187"

    init {
        custom = customIP
        userServerIP = serverIP
        bypass = bypassCheck
        viewDB = viewDB_IN
        mView = mView_IN
        if (!bypass) {
            if (custom && userServerIP != "None") {
                if (!TestServer(userServerIP)) {
                    Error("The server either didn't respond or responded incorrectly")
                }
            } else {
                if (!TestServer(default_ip)) {
                    Error("The server either didn't respond or responded incorrectly, please try again later")
                }
            }
        }
    }

    private fun Error(message: String) {
        valid = false
        val intent = Intent(mView, ErrorActivity::class.java)
        intent.putExtra(ErrorActivity.EXTRA_REPLY, message)
        mView.startActivity(intent)
    }

    private fun TestServer(ip: String): Boolean {
        try {
            val con = Connection(ip, 8080)
            con.send("T/Hello/JAVA")
            val response = con.WaitUntilRecv()
            if (response == "Hello") {
                return true
            }
            return false
        } catch (conerror: ConnectException) {
            return false
        }
    }

    private fun GetIP(): String {
        if (custom && userServerIP != "None") {
            if (!TestServer(userServerIP)) {
                Error("The server either didn't respond or responded incorrectly")
            }
            return userServerIP
        } else {
            if (!TestServer(default_ip)) {
                Error("The server either didn't respond or responded incorrectly, please try again later")
            }
            return default_ip
        }
    }


    fun DeleteGroup(name: String) {
        job = GlobalScope.launch {
            viewDB.GetGroupDAO().RemoveGroup(name)
            val user = viewDB.GetUserDAO().GetCurrentUser()[0]
            val con = Connection(GetIP(), 8080)
            con.send("D/${user.Name}/$name/${user.Token}/JAVA")
            val returned: String? = con.recv()
            if (returned == "IT") {
                job.cancel("Auth Error")
                valid = false
                Error("The cached token was invalid")
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                job.cancel()
                valid = false
                Error("The cached user does not exist")
                Log.e("ERROR", "Invalid User")
            }
            con.send("Ready")
            con.dc()
        }
    }


    fun RenameGroup(newname: String, oldname: String) {
        job = GlobalScope.launch {
            val user = viewDB.GetUserDAO().GetCurrentUser()[0]
            viewDB.GetGroupDAO().UpdateName(newname, oldname)
            val con = Connection(GetIP(), 8080)
            con.send("N/${user.Name}/$oldname/${user.Token}/JAVA")
            val returned: String? = con.recv()
            if (returned == "IT") {
                job.cancel("Auth Error")
                valid = false
                Error("The cached token was invalid")
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                job.cancel()
                valid = false
                Error("The cached user does not exist")
                Log.e("ERROR", "Invalid User")
            }
            con.send("Ready")
            con.WaitUntilRecv()
            con.send(newname)
            con.dc()
        }
    }

    fun Continue_if_Data() {
        job = GlobalScope.launch {
            val users = viewDB.GetUserDAO().GetCurrentUser()
            if (users.size > 0) {
                Log.d("DEBUG", "User detected")
                viewDB.GetGroupDAO().deleteAll()
                PopulateDB()
                val intent = Intent(mView, TaskList::class.java)
                mView.startActivity(intent)
            }
        }
    }

    fun Logout() {
        job = GlobalScope.launch {
            viewDB.GetUserDAO().deleteAll()
            val intent = Intent(mView, MainActivity::class.java)
            mView.startActivity(intent)
        }
    }

    fun Refresh() {
        job = GlobalScope.launch {
            viewDB.GetGroupDAO().deleteAll()
            PopulateDB()
        }
    }

    suspend fun PopulateDB() {
        val user = viewDB.GetUserDAO().GetCurrentUser()[0]
        val username = user.Name
        val token = user.Token
        val out: List<String?> = NON_SAFE_GetGroups(username, token)
        for (task in out) {
            task?.let {
                Log.d("DEBUG", it)
                var items: MutableList<String> = ArrayList()
                items = NON_SAFE_GetTasks(username, it, token)
                Log.i("Info", "Items: " + items.joinToString("/"))
                    val position = items.removeFirst().toInt()
                    val itemstring = items.joinToString(separator = "/")
                    viewDB.GetGroupDAO().insert(Group(it, position, itemstring))
                }
            }
    }

    fun NON_SAFE_GetGroups(
        username: String,
        token: String,
    ): List<String?> {
        val ip: String = GetIP()
        val con = Connection(ip, 8080)
        con.send("G/$username/NONE/$token/JAVA")
        val returned: String = con.recv()
        if (returned == "IT") {
            job.cancel("Auth Error")
            valid = false
            Error("The cached token was invalid")
            Log.e("ERROR", "Invalid Token")
        } else if (returned == "IU") {
            job.cancel()
            valid = false
            Error("The cached user does not exist")
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
        val con = Connection(GetIP(), 8080)
        con.send("R/$username/$group/$token/JAVA")
        val returned: String? = con.recv()
        if (returned == "IT") {
            job.cancel("Auth Error")
            valid = false
            Error("The cached token was invalid")
            Log.e("ERROR", "Invalid Token")
        } else if (returned == "IU") {
            job.cancel()
            valid = false
            Error("The cached user does not exist")
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
        group: String
    ) {
        job = GlobalScope.launch {
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
            NON_SAFE_PushGroup(username, group, token)
        }
    }

    fun NON_SAFE_PushGroup(
        username: String,
        group: String,
        token: String
    ) {
        val con = Connection(GetIP(), 8080)
        con.send("W/$username/$group/$token/JAVA")
        val returned: String? = con.recv()
        if (returned == "IT") {
            job.cancel("Auth Error")
            valid = false
            Error("The cached token was invalid")
            Log.e("ERROR", "Invalid Token")
        } else if (returned == "IU") {
            job.cancel()
            valid = false
            Error("The cached user does not exist")
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
        newtask: Task
    ) {
        job = GlobalScope.launch {
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
            NON_SAFE_PushGroup(user.Name, group, user.Token)
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

    fun MoveGroup(newindex: Int, group: String) {
        job = GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val groupDAO = viewDB.GetGroupDAO()
            val groups: MutableList<Group> = groupDAO.GetRawSortedGroups() as MutableList<Group>
            val to_move = groupDAO.GetRawGroupByName(group)
            groups.remove(to_move)
            groups.add(newindex, to_move)
            for (g in groups) {
                viewDB.GetGroupDAO().UpdatePos(g.Name, groups.indexOf(g))
                NON_SAFE_PushGroup(user.Name, g.Name, user.Token)
            }

        }
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
        if (tasklist.size == 0) {
            return "NONE"
        }
        val taskrawlist: MutableList<String> = ArrayList()
        for (i in tasklist) {
            taskrawlist.add(i.ConSelfToString())
        }
        val out1 = taskrawlist.joinToString("/")
        return out1
    }


    fun DeleteTask(
        group: String,
        taskname: String,
    ) {
        job = GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items.removeAt(items.indexOf(FindItemFromListById(taskname.toInt(), items)))
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token)
        }
    }


    fun EditTaskName(
        group: String,
        taskname: String,
        newname: String
    ) {
        job = GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items[items.indexOf(FindItemFromListById(taskname.toInt(), items))].name = newname
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token)
        }
    }


    fun MoveTask(group: String, newgroup: String, taskname: String) {
        job = GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            val move: Task =
                items.removeAt(items.indexOf(FindItemFromListById(taskname.toInt(), items)))
            val newgorupchange = viewDB.GetGroupDAO().GetRawGroupByName(newgroup)
            val new_group_items_string = newgorupchange.Items
            val new_group_items = ConstructTaskList(new_group_items_string)
            new_group_items.add(move)
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            viewDB.GetGroupDAO().UpdateItems(newgroup, DeconstructTaskList(new_group_items))
            NON_SAFE_PushGroup(username, group, token)
            NON_SAFE_PushGroup(username, newgroup, token)
        }
    }


    fun ChangeTaskOrder(
        group: String,
        taskname: String,
        newindex: Int,
        newgroup: String
    ) {
        job = GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            val move: Task =
                items.removeAt(items.indexOf(FindItemFromListById(taskname.toInt(), items)))
            items.add(newindex, move)
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token)
        }
    }


    fun ToggleTask(
        group: String,
        taskname: String,
        done: Boolean
    ) {
        job = GlobalScope.launch {
            val user = NON_SAFE_Get_User_Data(viewDB)
            val username = user.Name
            val token = user.Token
            val groupchange = viewDB.GetGroupDAO().GetRawGroupByName(group)
            val items_string = groupchange.Items
            val items = ConstructTaskList(items_string)
            items[items.indexOf(FindItemFromListById(taskname.toInt(), items))].done = done
            viewDB.GetGroupDAO().UpdateItems(group, DeconstructTaskList(items))
            NON_SAFE_PushGroup(username, group, token)
        }
    }


    fun NON_SAFE_Get_User_Data(viewDB: GroupViewModel): User {
        return viewDB.GetUserDAO().GetCurrentUser()[0]
    }

    fun Login(username: String, password: String) {
        job = GlobalScope.launch {
            val con = Connection(GetIP(), 8080)
            con.send("A/L/$username/$password/JAVA")
            val returned = con.WaitUntilRecv()
            con.dc()
            //TODO: Show error messages to user
            if (returned == "IL") {
                job.cancel("Auth Error")
                valid = false
                Error("That login is invalid")
                Log.e("AUTH", "Invalid Login")
            } else if (returned == "IE") {
                job.cancel("Auth Error")
                valid = false
                Error("The Server Had An Internal Error")
                Log.e("AUTH", "Internal Server Error")
            } else {
                val userdata = returned.split("/")
                val UserDAO = viewDB.GetUserDAO()
                UserDAO.deleteAll()
                UserDAO.insert(User(userdata[0], userdata[1]))
                Continue_if_Data()
            }
        }
    }

    fun NON_SAFE_Add_User(name: String, token: String) {
        val con = Connection(GetIP(), 8080)
        con.send("U/$name/NONE/$token/JAVA")
        con.WaitUntilRecv()
        con.send("Ready")
        con.dc()
    }

    fun Register(username: String, password: String) {
        job = GlobalScope.launch {
            val con = Connection(GetIP(), 8080)
            con.send("A/R/$username/$password/JAVA")
            val returned = con.WaitUntilRecv()
            con.dc()
            //TODO: Show error messages to user
            if (returned == "UE") {
                job.cancel("Auth Error")
                valid = false
                Error("That user already exists")
                Log.e("AUTH", "User Exists")
            } else if (returned == "IE") {
                job.cancel("Auth Error")
                valid = false
                Error("The Server Had An Internal Error")
                Log.e("AUTH", "Internal Server Error")
            } else {
                val userdata = returned.split("/")
                NON_SAFE_Add_User(userdata[0], userdata[1])
                val UserDAO = viewDB.GetUserDAO()
                UserDAO.deleteAll()
                UserDAO.insert(User(userdata[0], userdata[1]))
                Continue_if_Data()
            }
        }
    }
}
