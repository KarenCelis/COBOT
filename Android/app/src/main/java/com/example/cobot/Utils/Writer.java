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

    public static JSONObject writeServerCommunicationJSON(String message) throws JSONException {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("info", message);
        return objectToSend;
    }

    public static JSONObject writeConnectionJSON(String ip, int port, String robot) throws JSONException {

        JSONObject objectInfo = new JSONObject();

        objectInfo.put("ip", ip);
        objectInfo.put("port", port);
        objectInfo.put("robot", robot);

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("Connection", objectInfo);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return objectToSend;
    }

    public static JSONObject writeSimpleActionsJSON(Map<Integer, String> actionsSelected, Emotion emotion) throws JSONException {

        JSONArray arrayOfActions = new JSONArray();

        for(Map.Entry<Integer, String> entry : actionsSelected.entrySet()){

            JSONObject actionObject = new JSONObject();

            actionObject.put("id", entry.getKey());
            actionObject.put("value", entry.getValue());

            arrayOfActions.put(actionObject);
        }

        JSONObject emotionObject = new JSONObject();
        emotionObject.put("name", emotion.getEmotionName());
        emotionObject.put("value", emotion.getIntensity());

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("Actions", arrayOfActions);
        objectToSend.put("Emotion", emotionObject);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return objectToSend;
    }

    public static JSONObject writeEmergentAction(String action) throws JSONException {

        JSONObject objectInfo = new JSONObject();
        objectInfo.put("name", action);

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("EmergentAction", objectInfo);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return objectToSend;
    }

    public static JSONObject writeSignsOfLife(SignOfLife[] signsOfLife, int characterSelected) throws JSONException {

        JSONArray signArray = new JSONArray();

        for (SignOfLife sign: signsOfLife) {

            JSONObject signObject = new JSONObject();
            signObject.put("id", sign.getId());
            signObject.put("name", sign.getName());
            signObject.put("genericActionId", sign.getGenericActionId());

            JSONArray characterIdsArray = new JSONArray();

            for(int i = 0; i < sign.getCharacterId().length; i++){
                characterIdsArray.put(sign.getCharacterId()[i]);
            }

            signObject.put("characterId", characterIdsArray);

            signArray.put(signObject);
        }

        JSONObject objectToSend = new JSONObject();
        objectToSend.put("characterSelected", characterSelected);
        objectToSend.put("SignsOfLife", signArray);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return objectToSend;
    }

    public static JSONObject writeWorldModel(Scenario scenario, Position position) throws JSONException {

        JSONObject scenarioToSend = new JSONObject();

        scenarioToSend.put("id", scenario.getId());
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
        scenarioToSend.put("adjacencyMatrix", arrayOfMatrixRows);

        JSONArray arrayOfNodeNames = new JSONArray();

        for (int i = 0; i < scenario.getNode_names().length; i++) {
            arrayOfNodeNames.put(scenario.getNode_names()[i]);
        }
        scenarioToSend.put("node_names", arrayOfNodeNames);

        JSONArray arrayOfNode = new JSONArray();

        for(Node node: scenario.getNode()){

            JSONObject nodeObject = new JSONObject();
            nodeObject.put("id", node.getId());
            nodeObject.put("name", node.getName());
            nodeObject.put("xaxis", node.getXaxis());
            nodeObject.put("yaxis",node.getYaxis());

            arrayOfNode.put(nodeObject);
        }

        scenarioToSend.put("node", arrayOfNode);

        JSONObject positionToSend = new JSONObject();

        positionToSend.put("CharacterId", position.getCharacterId());
        positionToSend.put("NodeId", position.getNodeId());

        JSONObject objectToSend = new JSONObject();

        objectToSend.put("scenario", scenarioToSend);
        objectToSend.put("position", positionToSend);

        Log.d(TAG, "writeJSON: "+objectToSend.toString());

        return objectToSend;
    }

}
