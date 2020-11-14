package com.example.cobot.Utils;

import android.util.Log;

import com.example.cobot.Classes.Action;
import com.example.cobot.Classes.Character;
import com.example.cobot.Classes.EmergentAction;
import com.example.cobot.Classes.GenericAction;
import com.example.cobot.Classes.Node;
import com.example.cobot.Classes.Obra;
import com.example.cobot.Classes.Position;
import com.example.cobot.Classes.Scenario;
import com.example.cobot.Classes.Scene;
import com.example.cobot.Classes.SignOfLife;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Reader {

    private static final String TAG = "UtilsJSONReader";

    public static Obra crearObraDesdeJSON(String archivoJSON) throws JSONException {

        JSONObject jsonObject = new JSONObject(archivoJSON);

        JSONArray arrayOfCharacters = jsonObject.getJSONArray("Characters");
        JSONArray arrayOfScenes = jsonObject.getJSONArray("Scenes");
        JSONArray arrayOfGenericActions = jsonObject.getJSONArray("GenericActions");
        JSONArray arrayOfScenarios = jsonObject.getJSONArray("Scenarios");
        JSONArray arrayOfLifeSigns = jsonObject.getJSONArray("SignsOfLife");
        JSONArray arrayOfEmergentActions = jsonObject.getJSONArray("EmergentActions");

        Character[] characters = new Character[arrayOfCharacters.length()];
        Scene[] scenes = new Scene[arrayOfScenes.length()];
        Scenario[] scenarios = new Scenario[arrayOfScenarios.length()];
        GenericAction[] genericActions = new GenericAction[arrayOfGenericActions.length()];

        //loop para la información de los personajes.
        for (int i = 0; i < arrayOfCharacters.length(); i++) {

            characters[i] = new Character();
            JSONObject jsonTemp = arrayOfCharacters.getJSONObject(i);

            characters[i].setId(jsonTemp.getInt("id"));
            characters[i].setCharacterIconUrl(jsonTemp.getString("CharacterIcon"));
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
                    String[] texts = new String[arrayOfActionsTexts.length()+1];
                    Log.d(TAG, "crearObraDesdeJSON: "+"longitud de las opciones: "+arrayOfActionsTexts.length());

                    //loop para iterar los textos disponibles.
                    for (int k = 0; k < arrayOfActionsTexts.length(); k++) {
                        texts[k] = arrayOfActionsTexts.getString(k);
                        Log.d(TAG, "crearObraDesdeJSON: "+"opción: "+arrayOfActionsTexts.getString(k));
                    }
                    //Agregar opción de ninguno para cancelar la acción
                    texts[arrayOfActionsTexts.length()] = "Ninguno";
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

                    actions[j].setAudioFile(audios);
                }

                if(jsonTempActions.getBoolean("hasImages")){
                    JSONArray arrayOfImageUrls = jsonTemp.getJSONArray("optionImagesUrl");
                    String[] imageUrls = new String[arrayOfImageUrls.length()];

                    for (int k = 0; k < arrayOfImageUrls.length(); k++) {
                        imageUrls[k] = arrayOfImageUrls.getString(k);
                    }
                    actions[j].setImageUrls(imageUrls);
                }

                actions[j].setDuration(jsonTempActions.getInt("Duration"));
                actions[j].setHasImages(jsonTempActions.getBoolean("hasImages"));

            }
            //loop para la información de las posiciones de cada escena.
            for (int j = 0; j < arrayOfPositions.length(); j++) {

                positions[j] = new Position();
                JSONObject jsonTempPositions = arrayOfPositions.getJSONObject(j);

                positions[j].setCharacterId(jsonTempPositions.getInt("CharacterId"));
                positions[j].setNodeId(jsonTempPositions.getInt("NodeId"));
            }

            scenes[i].setCharacterIds(characterids);
            scenes[i].setActions(actions);
            scenes[i].setPositions(positions);
            scenes[i].setId(jsonTemp.getInt("id"));
            scenes[i].setScenario(jsonTemp.getInt("Scenario"));
        }
        for (int i = 0; i < arrayOfScenarios.length(); i++) {

            scenarios[i] = new Scenario();
            JSONObject jsonTemp = arrayOfScenarios.getJSONObject(i);

            scenarios[i].setId(jsonTemp.getInt("id"));
            scenarios[i].setName(jsonTemp.getString("name"));
            scenarios[i].setNodes(jsonTemp.getInt("nodes"));

            JSONArray arrayOfAdjacencyRows = jsonTemp.getJSONArray("adjacency_matrix");
            int[][] adjacencyMatrix = new int[arrayOfAdjacencyRows.length()][arrayOfAdjacencyRows.length()];

            //loop para recorrer cada fila de la matriz de adyacencias
            for (int j = 0; j < arrayOfAdjacencyRows.length(); j++) {

                JSONArray arrayOfAdjacencySingleRow = arrayOfAdjacencyRows.getJSONArray(j);

                //loop para recorrer cada dato de cada fila y agregarlo a la matriz
                for (int k = 0; k < arrayOfAdjacencySingleRow.length(); k++) {
                    adjacencyMatrix[j][k] = arrayOfAdjacencySingleRow.getInt(k);
                }
            }

            JSONArray arrayOfNodeNames = jsonTemp.getJSONArray("node_names");
            String[] nodeNames = new String[arrayOfNodeNames.length()];

            for (int j = 0; j < arrayOfNodeNames.length(); j++) {
                nodeNames[j] = arrayOfNodeNames.getString(j);
            }

            JSONArray arrayOfNodeInformation = jsonTemp.getJSONArray("node");
            Node[]node = new Node[arrayOfNodeInformation.length()];

            for (int j = 0; j < arrayOfNodeInformation.length(); j++) {

                node[j] = new Node();
                JSONObject jsonTempNode = arrayOfNodeInformation.getJSONObject(j);

                node[j].setId(jsonTempNode.getInt("id"));
                node[j].setName(jsonTempNode.getString("name"));
                node[j].setXaxis(jsonTempNode.getDouble("xaxis"));
                node[j].setYaxis(jsonTempNode.getDouble("yaxis"));
            }

            scenarios[i].setAdjacencyMatrix(adjacencyMatrix);
            scenarios[i].setNode_names(nodeNames);
            scenarios[i].setNode(node);
        }

        for (int i = 0; i < arrayOfGenericActions.length(); i++) {
            genericActions[i] = new GenericAction();
            JSONObject jsonTemp = arrayOfGenericActions.getJSONObject(i);
            genericActions[i].setId(jsonTemp.getInt("id"));
            genericActions[i].setName(jsonTemp.getString("name"));
            genericActions[i].setActionIconUrl(jsonTemp.getString("ActionIcon"));
            JSONArray arrayOfBlocks = jsonTemp.getJSONArray("blocks");
            int[] blocks = new int[arrayOfBlocks.length()];

            for (int j = 0; j < arrayOfBlocks.length(); j++) {
                //Si el número es el mismo que la acción, significa que no bloquea nada, se pone para que el arreglo nunca sea nulo.
                if(arrayOfBlocks.getInt(j)!=jsonTemp.getInt("id")){
                    blocks[j] = arrayOfBlocks.getInt(j);
                }
            }

            genericActions[i].setBlocks(blocks);
            genericActions[i].setDisplacement(jsonTemp.has("displacement"));
            genericActions[i].setSounds(jsonTemp.has("sounds"));
            Log.i(TAG, "sonido: " + jsonTemp.has("sounds"));
        }

        SignOfLife[] signOfLife = new SignOfLife[arrayOfLifeSigns.length()];
        for (int i = 0; i < arrayOfLifeSigns.length(); i++) {

            signOfLife[i] = new SignOfLife();
            JSONObject jsonTemp = arrayOfLifeSigns.getJSONObject(i);

            signOfLife[i].setId(jsonTemp.getInt("id"));
            signOfLife[i].setName(jsonTemp.getString("name"));

            JSONArray arrayOfCharacterids = jsonTemp.getJSONArray("characterId");
            int[] characterid = new int[arrayOfCharacterids.length()];

            for (int j = 0; j < arrayOfCharacterids.length(); j++) {
                characterid[j] = arrayOfCharacterids.getInt(j);
            }

            signOfLife[i].setCharacterId(characterid);
        }

        EmergentAction[] emergentActions = new EmergentAction[arrayOfEmergentActions.length()];
        for (int i = 0; i < arrayOfEmergentActions.length(); i++) {

            emergentActions[i] = new EmergentAction();
            JSONObject jsonTemp = arrayOfEmergentActions.getJSONObject(i);

            emergentActions[i].setId(jsonTemp.getInt("id"));
            emergentActions[i].setName(jsonTemp.getString("name"));

            JSONArray arrayOfCharacterids = jsonTemp.getJSONArray("characterId");
            int[] characterid = new int[arrayOfCharacterids.length()];

            for (int j = 0; j < arrayOfCharacterids.length(); j++) {
                characterid[j] = arrayOfCharacterids.getInt(j);
            }

            emergentActions[i].setCharacterId(characterid);
        }

        //Creación del objeto obra
        Obra obra = new Obra();
        obra.setTitle(jsonObject.getString("Title"));
        obra.setSceneAmount(jsonObject.getInt("SceneAmount"));
        obra.setCharacters(characters);
        obra.setScenes(scenes);
        obra.setScenarios(scenarios);
        obra.setGenericActions(genericActions);
        obra.setSignsOfLife(signOfLife);
        obra.setEmergentActions(emergentActions);

        Log.i(TAG, "Obra:\n" + obra.toString());
        return obra;
    }
}

