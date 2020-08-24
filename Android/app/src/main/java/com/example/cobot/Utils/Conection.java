package com.example.cobot.Utils;

public class Conection {


    private String ip;
    private int port;

    public Conection() {
    }

    public Conection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
