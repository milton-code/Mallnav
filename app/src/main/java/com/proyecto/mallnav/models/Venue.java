package com.proyecto.mallnav.models;

import com.google.firebase.firestore.PropertyName;


public class Venue {
    int id = -1;
    String nombre = null;
    int categoria_id = -1;
    String categoria = null;
    VenueIconObj venueIcon = null;

    public Venue(){}

    public Venue(int id, String nombre, int categoria_id) {
        this.id = id;
        this.nombre = nombre;
        this.categoria_id = categoria_id;
    }

    @PropertyName("venue_id")
    public int getId() {
        return id;
    }

    @PropertyName("venue_id")
    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(int categoria_id) {
        this.categoria_id = categoria_id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public VenueIconObj getVenueIcon() {
        return venueIcon;
    }

    public void setVenueIcon(VenueIconObj venueIcon) {
        this.venueIcon = venueIcon;
    }
}
