package com.example.dothingandroid

import android.util.Log
import kotlin.concurrent.thread
import kotlinx.coroutines.*

class DBManager {

    fun PopulateDB(ip: String, port: Int, username: String, group: String, token: String, groupDAO: GroupAccess){
        GlobalScope.launch {
            val con = Connection(ip, port)
            Log.d("DEBUG", "Sending Initial Auth Request")
            con.send("G/$username/$group/$token/JAVA")
            val returned: String? = con.recv()
            Log.d("DEBUG", "Received Reply To Auth Request")
            if (returned == "IT") {
                Log.e("ERROR", "Invalid Token")
            } else if (returned == "IU") {
                Log.e("ERROR", "Invalid User")
            }
            Log.d("DEBUG", "Authenticated")
            con.send("Ready")
            val out: List<String?> = con.RecvList("GO", "END")
            con.dc()
            Log.d("DEBUG", "Connection Closed")
            Log.d("DEBUG", "Printing List Of Groupss")

            for (task in out) {
                task?.let {
                    Log.d("DEBUG", it)
                    groupDAO.insert(Group(it, 234, ""))
                }
            }

        }
    }

}