package com.proyecto.mallnav.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.proyecto.mallnav.models.Venue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.proyecto.mallnav.models.VenueIconObj;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VenueProvider {
    private static final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    public static List<Venue> venueList = new ArrayList<>();

    public static void initVenues(Context context) {
        CollectionReference venuesCollection = mfirestore.collection("venues");
        CollectionReference categoriasCollection = mfirestore.collection("categorias");


        venuesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documentVenue : task.getResult()) {
                    Venue venue = documentVenue.toObject(Venue.class);
                    venueList.add(venue);
                }
            }
            else {
                Log.v("initVenues","Error al recuperar venues de Firebase");
            }
        });

        categoriasCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documentCategoria : task.getResult()){
                    int categoria_id = Objects.requireNonNull(documentCategoria.getLong("categoria_id")).intValue();
                    for(Venue venue : venueList){
                        if(venue.getCategoria_id() == categoria_id){
                            venue.setCategoria(documentCategoria.getString("nombre"));
                        }
                    }
                }
            }
            else {
                Log.v("initVenues","Error al recuprar categorias de Firebase");
            }
        });


        for(Venue venue : venueList) {
            for (VenueIconObj venueIcon : VenueIconsListProvider.VenueIconsList) {
                if (venue.getCategoria().equals(venueIcon.getCategoryName())) {
                    venue.setVenueIcon(venueIcon);
                }
            }
        }

    }

}
