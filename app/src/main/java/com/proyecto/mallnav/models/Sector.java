package com.proyecto.mallnav.models;

public class Sector {
    private int id = -1;
    private String nombre = null;
    private int pointX = -1;
    private int  pointY = -1;

    public Sector(int id, String nombre, int pointX, int pointY) {
        this.id = id;
        this.nombre = nombre;
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPointX() {
        return pointX;
    }

    public int getPointY() {
        return pointY;
    }
}
