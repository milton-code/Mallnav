package com.proyecto.mallnav.models;

import java.util.List;

public class Nodo {
    private String id;
    private float nodoX, nodoY;
    private List<String> vecinos; // IDs de Nodos conectados directamente

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getNodoX() {
        return nodoX;
    }

    public void setNodoX(float nodoX) {
        this.nodoX = nodoX;
    }

    public float getNodoY() {
        return nodoY;
    }

    public void setNodoY(float nodoY) {
        this.nodoY = nodoY;
    }

    public List<String> getVecinos() {
        return vecinos;
    }

    public void setVecinos(List<String> vecinos) {
        this.vecinos = vecinos;
    }
}
