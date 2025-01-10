package com.proyecto.mallnav.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;

import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.Nodo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class PathMaker {
    private final List<Nodo> listaNodos; // Nodos y sus vecinos
    public static float density;

    public PathMaker(List<Nodo> listaNodos, float density) {
        this.listaNodos = listaNodos;
        PathMaker.density = density;
    }
    public List<Nodo> crearRutaMasCorta(Nodo nodoStart, Nodo nodoEnd) {
        Queue<Nodo> cola = new LinkedList<>();
        Map<Nodo, Nodo> predecesores = new HashMap<>();
        Set<Nodo> visitados = new HashSet<>();
        cola.add(nodoStart);
        visitados.add(nodoStart);
        while (!cola.isEmpty()) {
            Nodo nodoActual = cola.poll();
            if (nodoActual.equals(nodoEnd)) {
                List<Nodo> ruta = reconstruirRuta(nodoStart, nodoEnd, predecesores);
                return ruta;
            }
            for (String vecinoId : nodoActual.getVecinos()) {
                Nodo nodoVecino = listaNodos.stream().filter(nodo -> nodo.getId().equals(vecinoId)).findFirst().orElse(null);
                if (!visitados.contains(nodoVecino)) {
                    cola.add(nodoVecino);
                    visitados.add(nodoVecino);
                    predecesores.put(nodoVecino, nodoActual);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Nodo> reconstruirRuta(Nodo nodoStart, Nodo nodoEnd, Map<Nodo, Nodo> predecesores) {
        List<Nodo> ruta = new ArrayList<>();
        Nodo actual = nodoEnd;

        while (actual != null && !actual.equals(nodoStart)) {
            ruta.add(0, actual);
            actual = predecesores.get(actual);
        }

        if (actual != null) {
            ruta.add(0, nodoStart);
        }
        return ruta;
    }

    // Método para añadir la superposición de ruta al mapa
    public PathOverlay crearRutaOverlay(Context context) {
        return new PathOverlay(context);
    }


    @SuppressLint("ViewConstructor")
    public static class PathOverlay extends View {
        private List<Pair<Float, Float>> rutaCoord;
        private final Paint paint;
        public PathOverlay(Context context) {
            super(context);
            this.paint = new Paint();
            initPaint();
        }
        private void initPaint() {
            paint.setColor(getResources().getColor(R.color.colorSuccess, Resources.getSystem().newTheme()));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
        }

        public void setRutaCoord(List<Pair<Float, Float>> rutaCoord) {
            this.rutaCoord = rutaCoord;
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            super.onDraw(canvas);

            if (rutaCoord == null || rutaCoord.size() < 2) return;

            // Dibujar líneas entre los nodos
            for (int i = 0; i < rutaCoord.size() - 1; i++) {
                Pair<Float, Float> coordActual = rutaCoord.get(i);
                Pair<Float, Float> coordSiguiente = rutaCoord.get(i + 1);
                canvas.drawLine(coordActual.first, coordActual.second,
                        coordSiguiente.first, coordSiguiente.second, paint);
            }
        }
    }
}
