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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketClient extends Service {

    public final static String SERVER_RESPONSE = "com.example.cobot.Utils.SocketClient.MESSAGE";
    public final static String SERVER_CONNECTION = "com.example.cobot.Utils.SocketClient.CONNECTION";
    public final static String SERVER_SET_CONNECTION = "com.example.cobot.Utils.SocketClient.SET_CONNECTION";
    public final static String ACTION = "ACTION";
    public static boolean isServiceRunning = false;
    private final IBinder myBinder = new LocalBinder();
    private OutputStreamWriter out;
    private Socket socket;
    private boolean mRun = false;
    boolean init = false;

    public static Connection connection;
    public static Connection serverConnection;
    public static String jsonToSend;

    public SocketClient() {
    }

    public static void getConnectionInstance(String ip, int port, String robot) {
        if (connection == null) {
            connection = new Connection(ip, port, robot);
        }
    }

    public static void getServerConnectionInstance(String ip, int port) {
        if (serverConnection == null) {
            serverConnection = new Connection(ip, port, "server");
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
                    if(incomingMessage.equalsIgnoreCase("")){
                        intent.putExtra(SERVER_SET_CONNECTION, incomingMessage);
                    }else{
                        intent.putExtra(SERVER_RESPONSE, incomingMessage);
                    }
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
                InetSocketAddress ina = new InetSocketAddress(serverConnection.getIp(), serverConnection.getPort());

                Log.i("TCP Client", "C: Connecting...");
                //create a socket to make the connection with the server

                socket = new Socket();
                socket.setSoTimeout(10*1000);
                socket.connect(ina, serverConnection.getPort());

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

            } catch (SocketTimeoutException | SocketException e) {
                Intent intent = new Intent();
                intent.setAction(ACTION);
                intent.putExtra(SERVER_CONNECTION, "Cannot connect to Server");
                sendBroadcast(intent);
                Log.e("TCP", "C: Error", e);
            } catch (IOException e) {
                Intent intent = new Intent();
                intent.setAction(ACTION);
                intent.putExtra(SERVER_CONNECTION, "Hubo un error al enviar");
                sendBroadcast(intent);
                Log.e("TCP", "C: Error", e);
                Log.e("TCP", "C: Error", e);
            }
        }
    }

}
