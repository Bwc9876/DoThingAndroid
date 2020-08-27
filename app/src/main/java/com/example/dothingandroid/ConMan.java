//package com.example.dothingandroid;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import android.util.Log;
//
//
//class ConMan {
//    public ConMan(){
//
//    }
//
//    public void TestMessage(Connection con, String message){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    con.connect();
//                    con.send("T/" + message);
//                    String returned = con.recv();
//                    con.dc();
//                    Log.d("DEBUG", returned);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    public void GetTasks(Connection con,String username, String token, String group){
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    con.connect();
//                    Log.d("DEBUG", "Sending Initial Auth Request");
//                    con.send("R/" + username + "/" + group + "/" + token + "/JAVA");
//                    String returned = con.recv();
//                    Log.d("DEBUG", "Received Reply To Auth Request");
//                    if (returned.equals("IT")){
//                        Log.e("ERROR", "Invalid Token");
//                    }
//                    else if (returned.equals("IU")){
//                        Log.e("ERROR", "Invalid User");
//                    }
//                    Log.d("DEBUG", "Authenticated");
//                    con.send("Ready");
//                    List<String> out = con.RecvList("GO", "END");
//                    con.dc();
//                    Log.d("DEBUG", "Received List Of Tasks");
//                    for(String task:out){
//                        Log.d("DEBUG", task);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//}
