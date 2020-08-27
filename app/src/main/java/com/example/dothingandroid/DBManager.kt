package com.example.dothingandroid

import android.util.Log
import kotlinx.coroutines.*

class DBManager {

    fun PopulateDB(ip: String, port: Int, username: String, group: String, token: String, groupDAO: GroupAccess){
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
                    Log.i("INFO", items.joinToString(separator="/"))
                    val position = items.removeFirst().toInt()
                    val itemstring = items.joinToString(separator="/")
                    groupDAO.insert(Group(it, position, itemstring))
                }
            }

        }
    }

    fun NON_SAFE_GetTasks(ip: String, port: Int, username: String, group: String, token: String): MutableList<String>{
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

    fun PushGroup(ip: String, port: Int, username: String, group: String, token: String, viewDB: GroupViewModel){
        GlobalScope.launch{

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
            val pos = viewDB.GetHighestId() + 1
            viewDB.GetGroupDAO().UpdatePos(group, pos)
            val tasks = (pos.toString() + "/" + tasksraw).split("/")
            con.SendList("END", tasks)
            con.dc()
        }
    }

}