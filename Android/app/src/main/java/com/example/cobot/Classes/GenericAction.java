package com.example.cobot.Classes;

import java.io.Serializable;

public class GenericAction implements Serializable {
    private int id;
    private String name;
    private String ActionIconUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActionIconUrl() {
        return ActionIconUrl;
    }

    public void setActionIconUrl(String actionIconUrl) {
        ActionIconUrl = actionIconUrl;
    }
}
