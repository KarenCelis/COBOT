package com.example.cobot.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketClient extends Service {

    public final static String SERVER_RESPONSE = "com.example.cobot.Utils.SocketClient.MESSAGE";
    public final static String SERVER_CONNECTION = "com.example.cobot.Utils.SocketClient.CONNECTION";
    public final static String ACTION = "ACTION";
    public static boolean isServiceRunning = false;
    private final IBinder myBinder = new LocalBinder();
    private OutputStreamWriter out;
    private Socket socket;
    private boolean mRun = false;
    boolean init = false;

    public static Connection connection;
    public static String jsonToSend;

    public SocketClient() {
    }

    public static void getConnectionInstance(String ip, int port) {
        if (connection == null) {
            connection = new Connection(ip, port);
        }
    }

    public static void setJsonToSend(String json){
        jsonToSend = json;
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("This is the onBind Methond");
        return myBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        System.out.println("I am in onCreate in the service");
    }

    /**
     * Used for sending messages to the socket connection
     */
    public void sendMessage(String message) throws IOException {
        /*
        if(out!=null && !out.checkError()){
            System.out.println("in sendMessage "+ message);
            out.println(message);
            out.flush();
        }
         */
        if(out!=null){
            out.write(message);
            out.flush();
            Log.i("Message", "Output sent "+message);
        }else{
            Log.i("Message", "Output is null");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        init = true;
        new Thread(connect).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            isServiceRunning = false;
            socket.close();
            Log.i("Socket has been closed", "Closed");
            mRun = false;
            if (out != null)
                out.flush();
            if (out != null) {
                out.close();
            }
            socket = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder {
        public SocketClient getService() {
            System.out.println("I am a local binder");
            return SocketClient.this;
        }
    }

    /**
     * This class is for receiving messages from the socket connection
     */
    private class ReceiveMessage  {
        BufferedReader in;
        String incomingMessage;
        void exe() {
            incomingMessage = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRun = true;
            while (mRun) {
                try {
                    incomingMessage = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (incomingMessage != null) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION);
                    intent.putExtra(SERVER_RESPONSE, incomingMessage);
                    sendBroadcast(intent);
                    Log.i("Response", incomingMessage);
                    incomingMessage = null;
                }
            }
            mRun = false;
        }
    }

    private class connectSocket implements Runnable {
        @Override
        public void run() {
            try {
                //Accesses settings for the ip address
                String ipAddress = "192.168.0.101";
                InetAddress serverAddr = InetAddress.getByName(ipAddress);
                int serverPortInt = 1235;
                Log.i("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server
                socket = new Socket(serverAddr, serverPortInt);
                //send the message to the server
                out = new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8);
                Log.i("TCP Client", "C: Sending "+jsonToSend);
                out.write(jsonToSend);
                out.flush();
                //out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.i("TCP Client", "C: Done.");
                isServiceRunning = true;
                if(socket.getRemoteSocketAddress() != null){
                    ReceiveMessage receiveMessage = new ReceiveMessage();
                    receiveMessage.exe();
                }

            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(ACTION);
                intent.putExtra(SERVER_CONNECTION, "Cannot connect to Server");
                sendBroadcast(intent);
                Log.e("TCP", "C: Error", e);
            }
        }
    }

}
