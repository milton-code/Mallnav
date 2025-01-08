package com.proyecto.mallnav.models;
import com.google.firebase.firestore.PropertyName;

public class Categoria {
    int categoria_id = -1;
    String nombre = null;

    public Categoria(){}
    public Categoria(int categoria_id, String nombre) {
        this.categoria_id = categoria_id;
        this.nombre = nombre;
    }
    @PropertyName("categoria_id")
    public int getId() {
        return categoria_id;
    }

    @PropertyName("categoria_id")
    public void setId(int categoria_id) {
        this.categoria_id = categoria_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
