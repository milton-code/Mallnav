package com.proyecto.mallnav.models;

import java.util.List;
import java.util.Map;

public class Cuadricula {
    private int cuadriculaId;
    private float verticeX;
    private float verticeY;
    private float ancho;
    private float alto;
    private Nodo nodo;
    private List<AccessPoint> listaRefinada;


    //private boolean esAccesible;
    //private boolean esParcialmenteAccesible;
    //private float xMedicion;
    //private float yMedicion;

    public Cuadricula(int cuadriculaId, float verticeX, float verticeY, float ancho, float alto, Nodo nodo,
                      List<AccessPoint> listaRefinada) {
        this.cuadriculaId = cuadriculaId;
        this.verticeX = verticeX;
        this.verticeY = verticeY;
        this.ancho = ancho;
        this.alto = alto;
        this.nodo = nodo;
        this.listaRefinada = listaRefinada;
    }

    public int getCuadriculaId() {
        return cuadriculaId;
    }

    public void setCuadriculaId(int cuadriculaId) {
        this.cuadriculaId = cuadriculaId;
    }

    public float getVerticeX() {
        return verticeX;
    }

    public void setVerticeX(float verticeX) {
        this.verticeX = verticeX;
    }

    public float getVerticeY() {
        return verticeY;
    }

    public void setVerticeY(float verticeY) {
        this.verticeY = verticeY;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getAlto() {
        return alto;
    }

    public void setAlto(float alto) {
        this.alto = alto;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public void setNodo(Nodo nodo) {
        this.nodo = nodo;
    }

    public List<AccessPoint> getListaRefinada() {
        return listaRefinada;
    }

    public void setListaRefinada(List<AccessPoint> listaRefinada) {
        this.listaRefinada = listaRefinada;
    }

}
