package com.example.cobot.Utils;

import android.util.Log;

import com.example.cobot.Classes.Emotion;

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
}
