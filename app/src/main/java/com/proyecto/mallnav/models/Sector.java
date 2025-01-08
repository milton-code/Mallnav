package com.proyecto.mallnav.models;

public class Sector {
    private int id = -1;
    private String nombre = null;
    private int pointX = -1;
    private int  pointY = -1;
    private String nodoId = null;
    private int venueId = -1;

    public Sector(int id, String nombre, int pointX, int pointY, String nodoId) {
        this.id = id;
        this.nombre = nombre;
        this.pointX = pointX;
        this.pointY = pointY;
        this.nodoId = nodoId;
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
    public String getNodoId() {
        return nodoId;
    }
    public void setNodoId(String nodoId) {
        this.nodoId = nodoId;
    }
    public int getVenueId() {
        return venueId;
    }
    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }
}
