package com.example.cobot.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cobot.Classes.Obra;
import com.example.cobot.R;
import com.example.cobot.Utils.Reader;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class ChooseFileActivity extends Activity {

    private static final int READ_REQUEST_CODE = 42;
    private static final String TAG = "FileRead";
    private Obra obra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Button BAbrirArchivo = findViewById(R.id.BAbrirArchivo);

        BAbrirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                final Uri uri = data.getData();
                assert uri != null;
                TextView TVSeleccionArchivo = findViewById(R.id.TVSeleccionarArchivo);
                Button BCargarObra = findViewById(R.id.BCargarObra);

                TVSeleccionArchivo.setText(getFileName(uri));
                BCargarObra.setVisibility(View.VISIBLE);

                BCargarObra.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            IniciarActividadDeSeleccionDePersonaje(uri);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        else{
            Log.i(TAG, "No se pudo obtener la dirección del archivo");
        }
    }

    private void IniciarActividadDeSeleccionDePersonaje(Uri uri) throws IOException, JSONException {
        cargarObra(uri);
        Intent intent = new Intent(this, CharacterSelectionActivity.class);
        intent.putExtra("obra", obra);
        startActivity(intent);
    }

    private void cargarObra(Uri uri) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        Log.i(TAG, "Uri path: " + uri.getPath());
        InputStream inputStream = getContentResolver().openInputStream(uri);
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String NL = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(NL);
        }
        reader.close();
        String Result = sb.toString();
        obra = Reader.crearObraDesdeJSON(Result);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
