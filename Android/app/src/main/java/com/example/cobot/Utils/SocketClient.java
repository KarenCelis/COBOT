package com.example.cobot.Utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient extends AsyncTask<Void, Void, Void> {
    static Thread sent;
    static Thread receive;
    static Socket socket;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            socket = new Socket("192.168.0.101",1235);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        sent = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    BufferedReader stdIn =new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    while(true){
                        System.out.println("Trying to read...");
                        String in = stdIn.readLine();
                        if(in == null){
                            break;
                        }
                        System.out.println(in);
                        out.print("Trying to make python server to read this"+"\r\n");
                        out.flush();
                        System.out.println("Message sent");
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        sent.start();
        try {
            sent.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
