//package com.example.dothingandroid;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Scanner;
//import java.util.List;
//
//class Connection{
//    String ip;
//    Integer port;
//    Integer buffer;
//    PrintWriter writer;
//    BufferedReader reader;
//    Socket s;
//    Scanner scanner;
//    public Connection(String ip_in, Integer port_in, Integer buffer_in) throws IOException{
//        ip = ip_in;
//        port = port_in;
//        buffer = buffer_in;
//    }
//    public void connect() throws IOException {
//        this.s = new Socket(this.ip, this.port);
//        this.writer = new PrintWriter(this.s.getOutputStream(), true);
//        this.reader = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
//        this.scanner = new Scanner(reader);
//    }
//    public void send(String message){
//        this.writer.println(message);
//    }
//    public String recv(){
//        if (this.scanner.hasNext()) {
//            return this.scanner.next();
//        }
//        else{
//            return "";
//        }
//    }
//    public String WaitUntilRecv(){
//        while(true){
//            String message = recv();
//            if (!message.equals("")){
//                return message;
//            }
//        }
//    }
//    public List<String> RecvList(String contcode, String endcode){
//        List<String> out = new ArrayList<String>();
//        while (true){
//            String data = this.recv();
//            this.send(contcode);
//            if (data.equals(endcode)){
//                break;
//            }
//            else if (!data.equals("")){
//                out.add(data);
//            }
//        }
//        return out;
//    }
//    public void dc() throws IOException {
//        this.s.close();
//    }
//}

