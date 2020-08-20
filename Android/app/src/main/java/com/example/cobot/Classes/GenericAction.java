package com.example.cobot.Classes;

import java.io.Serializable;

public class GenericAction implements Serializable {
    private int id;
    private String name;
    private String ActionIconUrl;
    //parameter es el parámetro de tipo cualquiera que se escoge para cada acción, como el diálogo o el destino de la acción.
    private String parameter;

    public GenericAction(int id, String parameter){
        this.id = id;
        this.parameter = parameter;
    }

    public GenericAction(){

    }

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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
