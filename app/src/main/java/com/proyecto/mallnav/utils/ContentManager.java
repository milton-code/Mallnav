package com.proyecto.mallnav.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.proyecto.mallnav.models.Venue;
import com.proyecto.mallnav.viewmodel.NavigationViewModel;
import com.proyecto.mallnav.viewmodel.VenueViewModel;

import java.util.HashMap;
import java.util.Map;



public class ContentManager {
    private CollectionReference venuesCollection = null;
    private FirebaseFirestore mFirestore = null;
    private Context context;
    private static VenueViewModel viewModel = null;


    public ContentManager(Context context){
        this.context = context;
        mFirestore = FirebaseFirestore.getInstance();
        venuesCollection = mFirestore.collection("venues");
    }

    public static void setViewModel(VenueViewModel vm) {
        viewModel = vm;
    }

    public void actualizarVenue(Venue mPinVenue, String NombreUpd, int categoriaIdUpd){

        int venueId = mPinVenue.getId();
        int venueCategoriaId = mPinVenue.getCategoria_id();
        String mensaje;
        if(venueCategoriaId != -1){
            mensaje = "Venue actualizado exitosamente";
        }
        else {
            mensaje = "Venue agregado exitosamente";
        }

        venuesCollection
                .whereEqualTo("venue_id", venueId) // Filtra por el campo "venue_id"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Agrega al documento encontrado
                                Map<String, Object> mapa = new HashMap<>();
                                mapa.put("categoria_id", categoriaIdUpd);
                                mapa.put("nombre", NombreUpd);

                                venuesCollection.document(document.getId()).update(mapa)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
                                            viewModel.loadVenues();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("CManager","Error al agregar/actualizar el venue: " + e.getMessage());
                                        });
                            }
                        } else {
                            Log.e("CManager","No se encontró ningún venue con el ID: " + venueId);
                        }
                    } else {
                        Log.e("CManager", "Error al buscar el venue: " + task.getException().getMessage());
                    }

                });
    }

    public void eliminarVenue(Venue mPinVenue){
        int venueId = mPinVenue.getId();
        // Accede a la colección "venues"
        venuesCollection
                .whereEqualTo("venue_id", venueId) // Filtra por el campo "venue_id"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //"Elimina" el documento encontrado
                                Map<String, Object> mapa = new HashMap<>();
                                mapa.put("categoria_id", -1);
                                mapa.put("nombre", "nulo");

                                venuesCollection.document(document.getId()).update(mapa)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context,"Venue eliminado exitosamente",Toast.LENGTH_SHORT).show();
                                            viewModel.loadVenues();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("CManager","Error al eliminar el venue: " + e.getMessage());
                                        });
                            }
                        } else {
                            Log.e("CManager","No se encontró ningún venue con el ID: " + venueId);
                        }
                    } else {
                        Log.e("CManager", "Error al buscar el venue: " + task.getException().getMessage());
                    }
                });
    }
}
