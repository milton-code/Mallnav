package com.proyecto.mallnav.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.proyecto.mallnav.models.Sector;
import com.proyecto.mallnav.models.Venue;
import com.proyecto.mallnav.models.VenueIconObj;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VenueProvider {
    @SuppressLint("StaticFieldLeak")
    private static final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    public static List<Venue> venueList = new ArrayList<>();
    public static List<VenueIconObj> icons = VenueIconsListProvider.VenueIconsList;
    public static List<Sector> sectores = SectorListProvider.SectorList;

    public static void initVenues(OnVenuesLoadedCallback callback) {
        CollectionReference venuesCollection = mfirestore.collection("venues");
        CollectionReference categoriasCollection = mfirestore.collection("categorias");
        CollectionReference sector_venueCollection = mfirestore.collection("sector_venue");

        venuesCollection.get().addOnCompleteListener(task -> {
            //recuperacion de venues
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot documentVenue : task.getResult()) {
                    Venue venue = documentVenue.toObject(Venue.class);
                    venueList.add(venue);
                }

                //recuperacion de categorias de venues
                categoriasCollection.get().addOnCompleteListener(taskCat -> {
                    if (taskCat.isSuccessful()){
                        for (QueryDocumentSnapshot documentCategoria : taskCat.getResult()){
                            int categoria_id = Objects.requireNonNull(documentCategoria.getLong("categoria_id")).intValue();
                            for(Venue venue : venueList){
                                if(venue.getCategoria_id() == categoria_id){
                                    venue.setCategoria(documentCategoria.getString("nombre"));
                                }
                            }

                            sector_venueCollection.get().addOnCompleteListener(taskSec -> {
                                if (taskSec.isSuccessful()){
                                    for (QueryDocumentSnapshot documentSectorVenue : taskSec.getResult()){
                                        int venue_id = Objects.requireNonNull(documentSectorVenue.getLong("venue_id")).intValue();
                                        for (Venue venue : venueList){
                                            if(venue.getId()==venue_id){
                                                venue.setSector_id(Objects.requireNonNull(documentSectorVenue.getLong("sector_id")).intValue());
                                            }
                                        }
                                    }

                                    for(Venue venue : venueList){
                                        for(VenueIconObj venueIcon : icons){
                                            if (venue.getCategoria() != null && venue.getCategoria().equals(venueIcon.getCategoryName())){
                                                venue.setVenueIcon(venueIcon);
                                                Log.d("VenueProvider", "categoria asignada: " + venueIcon.getCategoryName());
                                            }
                                        }

                                        for(Sector sector: sectores){
                                            if (venue.getSector_id() == sector.getId()){
                                                venue.setSector(sector);
                                            }
                                        }
                                    }

                                    callback.onVenuesLoaded(venueList);
                                }
                            });
                        }
                    }
                    else {
                        Log.e("VenueProvider","Error al recuprar categorias de Firebase");
                    }
                });
                Log.d("VenueProvider", "Number of venues: " + venueList.size());

            }
            else {
                Log.e("VenueProvider","Error al recuperar venues de Firebase");
            }
        });

    }

    public interface OnVenuesLoadedCallback {
        void onVenuesLoaded(List<Venue> venues);
    }

}
