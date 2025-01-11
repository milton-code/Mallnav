package com.proyecto.mallnav.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.activity.contextaware.ContextAware;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.AccessPoint;
import com.proyecto.mallnav.models.Cuadricula;
import com.proyecto.mallnav.models.Nodo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CuadriculaListProvider {
    public static List<Cuadricula> cuadriculaList = new ArrayList<>();

    public static void cargarCuadriculas (Context context, String fileName){
        StringBuilder json = new StringBuilder();

        // Cargar el archivo JSON desde assets
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Carga cuadriculas", Objects.requireNonNull(e.getMessage()));
        }

        // Deserializar el JSON a una lista de objetos Cuadricula
        Gson gson = new Gson();
        Type cuadriculaListType = new TypeToken<List<Cuadricula>>(){}.getType();
        cuadriculaList = gson.fromJson(json.toString(), cuadriculaListType);
    }

    public static Set<String> obtenerBSSIDDeLista(){
        Set<String> bssid = new HashSet<>();
        for (Cuadricula cuadricula : cuadriculaList){
            for(AccessPoint ap : cuadricula.getListaRefinada()){
                bssid.add(ap.getBssid());
            }
        }
        for(String BSSID : bssid){
            Log.w("BSSID","BSSID: " + BSSID);
        }
        return bssid;
    }
    

    public static List<Nodo> obtenerListaNodos(){
        Nodo nodo;
        List<Nodo> listaNodos = new ArrayList<>();
        for (Cuadricula cuadricula : cuadriculaList){
            nodo = cuadricula.getNodo();
            listaNodos.add(nodo);
        }
        return listaNodos;
    }

}
