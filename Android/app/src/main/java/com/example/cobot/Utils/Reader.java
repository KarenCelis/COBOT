package com.example.cobot.Utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.example.cobot.Classes.Action;
import com.example.cobot.Classes.Character;
import com.example.cobot.Classes.GenericAction;
import com.example.cobot.Classes.Obra;
import com.example.cobot.Classes.Position;
import com.example.cobot.Classes.Scene;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Reader {
    private static final String TAG = "UtilsJSONReader";

    public static Obra crearObraDesdeJSON(String archivoJSON) throws JSONException {

        JSONObject jsonObject = new JSONObject(archivoJSON);
        JSONArray arrayOfCharacters = jsonObject.getJSONArray("Characters");
        JSONArray arrayOfScenes = jsonObject.getJSONArray("Scenes");
        JSONArray arrayOfGenericActions = jsonObject.getJSONArray("GenericActions");

        Character[] characters = new Character[arrayOfCharacters.length()];
        Scene[] scenes = new Scene[arrayOfScenes.length()];
        GenericAction[] genericActions = new GenericAction[arrayOfGenericActions.length()];

        //loop para la información de los personajes.
        for (int i = 0; i < arrayOfCharacters.length(); i++) {
            characters[i] = new Character();
            JSONObject jsonTemp = arrayOfCharacters.getJSONObject(i);
            characters[i].setId(jsonTemp.getInt("id"));
            characters[i].setCharacterIconUrl(jsonTemp.getString("CharacterIcon"));
            characters[i].setInitialAlignment(jsonTemp.getInt("InitialAlignment"));
            characters[i].setInitialPosition(jsonTemp.getInt("InitialPosition"));
            characters[i].setName(jsonTemp.getString("Name"));
        }
        //loop para la información de las escenas
        for (int i = 0; i < arrayOfScenes.length(); i++) {

            scenes[i] = new Scene();
            JSONObject jsonTemp = arrayOfScenes.getJSONObject(i);
            JSONArray arrayOfActions = jsonTemp.getJSONArray("actions");
            JSONArray arrayOfPositions = jsonTemp.getJSONArray("Positions");
            JSONArray arrayOfCharacterids = jsonTemp.getJSONArray("CharacterIds");
            Action[] actions = new Action[arrayOfActions.length()];
            Position[] positions = new Position[arrayOfPositions.length()];
            int[] characterids = new int[arrayOfCharacterids.length()];

            //loop para la información de los personajes que actúan en cada escena.
            for (int j = 0; j < arrayOfCharacterids.length(); j++) {
                characterids[j] = arrayOfCharacterids.getInt(j);
            }

            //loop para la información de las acciones de cada escena.
            for (int j = 0; j < arrayOfActions.length(); j++) {
                actions[j] = new Action();
                JSONObject jsonTempActions = arrayOfActions.getJSONObject(j);
                actions[j].setId(jsonTempActions.getInt("id"));
                actions[j].setIdGeneric(jsonTempActions.getInt("idGeneric"));
                actions[j].setActionName(jsonTempActions.getString("ActionName"));
                actions[j].setCharacterId(jsonTempActions.getInt("CharacterId"));

                //Se verifica si la acción es de tipo hablar, en ese caso existirá un arreglo de posibles textos que hay que leer.
                if (jsonTempActions.has("DisplayText")) {

                    JSONArray arrayOfActionsTexts = jsonTempActions.getJSONArray("DisplayText");
                    String[] texts = new String[arrayOfActionsTexts.length()];

                    //loop para iterar los textos disponibles.
                    for (int k = 0; k < arrayOfActionsTexts.length(); k++) {
                        texts[k] = arrayOfActionsTexts.getString(k);
                    }
                    actions[j].setDisplayText(texts);
                }
                //Se verifica si la acción es de tipo hablar o sonido, en ese caso existirá un arreglo de posibles textos/sonidos que hay que leer.
                if (jsonTempActions.has("audiofile")) {

                    JSONArray arrayOfActionsAudios = jsonTempActions.getJSONArray("audiofile");
                    String[] audios = new String[arrayOfActionsAudios.length()];
                    //loop para iterar los audios disponibles.
                    for (int l = 0; l < arrayOfActionsAudios.length(); l++) {
                        audios[l] = arrayOfActionsAudios.getString(l);
                    }
                    actions[j].setDisplayText(audios);
                }
                actions[j].setDuration(jsonTempActions.getInt("Duration"));
            }
            //loop para la información de las posiciones de cada escena.
            for (int j = 0; j < arrayOfPositions.length(); j++) {
                positions[j] = new Position();
                JSONObject jsonTempPositions = arrayOfPositions.getJSONObject(j);
                positions[j].setCharacterId(jsonTempPositions.getInt("CharacterId"));
                positions[j].setLocation(jsonTempPositions.getString("Location"));
            }
            scenes[i].setCharacterIds(characterids);
            scenes[i].setActions(actions);
            scenes[i].setPositions(positions);
            scenes[i].setId(jsonTemp.getInt("id"));
            scenes[i].setScenario(jsonTemp.getInt("Scenario"));
        }
        for (int i = 0; i < arrayOfGenericActions.length(); i++) {
            genericActions[i] = new GenericAction();
            JSONObject jsonTemp = arrayOfGenericActions.getJSONObject(i);
            genericActions[i].setId(jsonTemp.getInt("id"));
            genericActions[i].setName(jsonTemp.getString("name"));
            genericActions[i].setActionIconUrl(jsonTemp.getString("ActionIcon"));
        }
        //Creación del objeto obra
        Obra obra =
                new Obra(jsonObject.getString("Title"),
                        jsonObject.getInt("SceneAmount"),
                        characters,
                        scenes, genericActions);
        Log.i(TAG, "Obra:\n" + obra.toString());
        return obra;
    }
}

