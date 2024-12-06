package com.proyecto.mallnav.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

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

    public PathMaker(List<Nodo> listaNodos) {
        this.listaNodos = listaNodos;
    }
    public List<Nodo> crearRutaMasCorta(Nodo nodoStart, Nodo nodoEnd) {
        //Cola para realizar la búsqueda por niveles
        Queue<Nodo> cola = new LinkedList<>();
        Map<Nodo, Nodo> predecesores = new HashMap<>();
        Set<Nodo> visitados = new HashSet<>();

        cola.add(nodoStart);
        visitados.add(nodoStart);

        // Realizar búsqueda por niveles
        while (!cola.isEmpty()) {
            Nodo nodoActual = cola.poll();

            if (nodoActual.equals(nodoEnd)) {
                // Nodo destino encontrado, reconstruir la ruta
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

        // Si no hay ruta al nodo destino
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
    public PathOverlay crearRutaOverlay(Context context, List<Nodo> ruta) {
        return new PathOverlay(context, ruta);
    }


    private class PathOverlay extends View {
        private final List<Nodo> ruta;
        private final Paint paint;
        public PathOverlay(Context context, List<Nodo> ruta) {
            super(context);
            this.ruta = ruta;
            this.paint = new Paint();
            initPaint();
        }
        private void initPaint() {
            paint.setColor(getResources().getColor(R.color.colorSuccess, Resources.getSystem().newTheme()));
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (ruta == null || ruta.size() < 2) return;

            // Dibujar líneas entre los nodos
            for (int i = 0; i < ruta.size() - 1; i++) {
                Nodo nodoActual = ruta.get(i);
                Nodo nodoSiguiente = ruta.get(i + 1);
                canvas.drawLine(nodoActual.getX(), nodoActual.getY(),
                        nodoSiguiente.getX(), nodoSiguiente.getY(), paint);
            }
        }
    }
}
