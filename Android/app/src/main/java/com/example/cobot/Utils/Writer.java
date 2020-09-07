package com.example.cobot.Utils;

import android.util.Log;

import com.example.cobot.Classes.Emotion;
import com.example.cobot.Classes.Obra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Writer {
    private static final String TAG = "JSON writing";

    public static JSONObject writeJSON(Map<Integer, String> actionsSelected, Emotion emotion) throws JSONException {

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

    public static JSONObject writeConnectionJSON(String ip, int port) throws JSONException {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("ip", ip);
        objectToSend.put("port", port);
        return objectToSend;
    }

    public static JSONObject writeServerCommunicationJSON(String message) throws JSONException {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("info", message);
        return objectToSend;
    }

    public static JSONObject writePlayInfo(Obra obra) throws JSONException {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("Scenarios", obra.getJsonScenarios());
        objectToSend.put("SignsOfLife", obra.getJsonSignsOfLife());
        Log.d(TAG, "writeJSON: "+objectToSend.toString());
        return objectToSend;
    }
}
