package com.example.dothingandroid

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*


class Connection(in_address: String, in_port: Int) {

    val address = in_address
    val port = in_port


    private var s: Socket = Socket(address, port)
    private var reader: BufferedReader = BufferedReader(InputStreamReader(s.getInputStream()))
    private var writer: PrintWriter = PrintWriter(s.getOutputStream(), true)
    private var scanner: Scanner = Scanner(reader)

    fun send(message: String) {
        writer.println(message)
    }

    fun recv(): String {
        return if (scanner.hasNext()) {
            scanner.next()
        } else {
            ""
        }
    }

    fun WaitUntilRecv(): String {
        while (true) {
            val message = recv()
            if (message != "") {
                return message
            }
        }
    }

    fun RecvList(contcode: String, endcode: String): MutableList<String> {
        val out: MutableList<String> = ArrayList()
        while (true) {
            val data = recv()
            send(contcode)
            if (data == endcode) {
                break
            } else if (data != "") {
                out.add(data)
            }
        }
        return out
    }

    fun SendList(endcode: String, list: List<String>) {
        for (item in list) {
            send(item)
            WaitUntilRecv()
        }
        send(endcode)
    }

    fun dc() {
        s.close()
    }

}