package com.example.cobot.Utils;

public class Connection {


    private String ip;
    private int port;
    private String robot;

    public Connection() {
    }

    public Connection(String ip, int port, String robot) {
        this.ip = ip;
        this.port = port;
        this.robot = robot;
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

    public String getRobot() {
        return robot;
    }

    public void setRobot(String robot) {
        this.robot = robot;
    }
}
