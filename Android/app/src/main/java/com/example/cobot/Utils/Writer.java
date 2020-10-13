package com.example.cobot.Utils;

import android.util.Log;

import com.example.cobot.Classes.Emotion;
import com.example.cobot.Classes.Node;
import com.example.cobot.Classes.Position;
import com.example.cobot.Classes.Scenario;
import com.example.cobot.Classes.SignOfLife;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Writer {
    private static final String TAG = "JSON writing";

    //code 0
    public static JSONObject writeServerCommunicationJSON(String ip, int port) throws JSONException {

        JSONObject connectionMessage = new JSONObject();
        JSONObject objectInfo = new JSONObject();

        objectInfo.put("ip", ip);
        objectInfo.put("port", port);

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("Connection", objectInfo);

        connectionMessage.put("code", 0);
        connectionMessage.put("data", objectToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return connectionMessage;
    }

    //code 1
    public static JSONObject writeConnectionJSON(String ip, int port, String robot) throws JSONException {

        JSONObject connectionMessage = new JSONObject();
        JSONObject objectInfo = new JSONObject();

        objectInfo.put("ip", ip);
        objectInfo.put("port", port);
        objectInfo.put("robot", robot);

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("Connection", objectInfo);

        connectionMessage.put("code", 1);
        connectionMessage.put("data", objectToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return connectionMessage;
    }

    //code 2
    public static JSONObject writeSignsOfLife(SignOfLife[] signsOfLife, int characterSelected) throws JSONException {

        JSONObject signsMessage = new JSONObject();
        JSONArray signArray = new JSONArray();

        for (SignOfLife sign: signsOfLife) {

            for(int i = 0; i < sign.getCharacterId().length; i++){
                //Se envían únicamente los signos de vida del personaje seleccionado, el resto no
                //porque no es necesario enviar información que no ejecutará el personaje escogido
                if(characterSelected == sign.getCharacterId()[i]){
                    JSONObject signObject = new JSONObject();
                    signObject.put("actionid", sign.getId());
                    signObject.put("name", sign.getName());

                    signArray.put(signObject);
                }
            }
        }

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("characterSelected", characterSelected);
        objectToSend.put("SignsOfLife", signArray);

        signsMessage.put("code", 2);
        signsMessage.put("data", objectToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return signsMessage;
    }

    //code 3
    public static JSONObject writeWorldModel(Scenario scenario, Position position) throws JSONException {

        JSONObject worldModelMessage = new JSONObject();
        JSONObject scenarioToSend = new JSONObject();

        scenarioToSend.put("scenarioid", scenario.getId());
        scenarioToSend.put("name", scenario.getName());
        scenarioToSend.put("nodes", scenario.getNodes());

        JSONArray arrayOfMatrixRows = new JSONArray();

        for (int i = 0; i < scenario.getAdjacencyMatrix().length; i++) {

            JSONArray arrayOfMatrixSingleRow = new JSONArray();

            for (int j = 0; j < scenario.getAdjacencyMatrix()[i].length; j++) {
                arrayOfMatrixSingleRow.put(scenario.getAdjacencyMatrix()[i][j]);
            }

            arrayOfMatrixRows.put(arrayOfMatrixSingleRow);

        }
        scenarioToSend.put("adjacencymatrix", arrayOfMatrixRows);

        JSONArray arrayOfNodeNames = new JSONArray();

        for (int i = 0; i < scenario.getNode_names().length; i++) {
            arrayOfNodeNames.put(scenario.getNode_names()[i]);
        }
        scenarioToSend.put("node_names", arrayOfNodeNames);

        JSONArray arrayOfNode = new JSONArray();

        for(Node node: scenario.getNode()){

            JSONObject nodeObject = new JSONObject();
            nodeObject.put("nodeid", node.getId());
            nodeObject.put("name", node.getName());
            nodeObject.put("xaxis", node.getXaxis());
            nodeObject.put("yaxis",node.getYaxis());

            arrayOfNode.put(nodeObject);
        }

        scenarioToSend.put("node", arrayOfNode);

        JSONObject positionToSend = new JSONObject();

        positionToSend.put("characterid", position.getCharacterId());
        positionToSend.put("nodeid", position.getNodeId());

        JSONObject objectToSend = new JSONObject();

        objectToSend.put("scenario", scenarioToSend);
        objectToSend.put("position", positionToSend);

        worldModelMessage.put("code", 3);
        worldModelMessage.put("data", objectToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return worldModelMessage;
    }

    //code 4
    public static JSONObject writeSimpleActionsJSON(Map<Integer, String> actionsSelected, Emotion emotion) throws JSONException {

        JSONObject actionsMessage = new JSONObject();
        JSONArray arrayOfActions = new JSONArray();

        for(Map.Entry<Integer, String> entry : actionsSelected.entrySet()){

            if(!entry.getValue().equalsIgnoreCase("Ninguno")){

                JSONObject actionObject = new JSONObject();

                actionObject.put("actionid", entry.getKey());
                actionObject.put("value", entry.getValue());

                arrayOfActions.put(actionObject);
            }

        }

        JSONObject emotionObject = new JSONObject();
        emotionObject.put("name", emotion.getEmotionName());
        emotionObject.put("value", emotion.getIntensity());

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("Actions", arrayOfActions);
        objectToSend.put("Emotion", emotionObject);

        actionsMessage.put("code", 4);
        actionsMessage.put("data", objectToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return actionsMessage;
    }

    //code 5
    public static JSONObject writeEmergentAction(int emergentSelected, String action, Emotion emotion) throws JSONException {

        JSONObject actionsMessage = new JSONObject();
        JSONObject objectInfo = new JSONObject();
        objectInfo.put("actionid", emergentSelected);
        objectInfo.put("name", action);

        JSONObject emotionObject = new JSONObject();
        emotionObject.put("name", emotion.getEmotionName());
        emotionObject.put("value", emotion.getIntensity());

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("EmergentAction", objectInfo);
        objectToSend.put("Emotion", emotionObject);

        actionsMessage.put("code", 5);
        actionsMessage.put("data", objectToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return actionsMessage;
    }

}
