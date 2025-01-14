package com.proyecto.mallnav.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.proyecto.mallnav.models.Categoria;
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
    static FirebasePerformance performance = FirebasePerformance.getInstance();
    static Trace trace = performance.newTrace("firestore_query_trace");
    public static List<Venue> venueList = new ArrayList<>();
    public static List<Categoria> categoriaList = new ArrayList<>();
    public static List<VenueIconObj> icons = VenueIconsListProvider.VenueIconsList;
    public static List<Sector> sectores = SectorListProvider.SectorList;


    public static void initVenues(OnVenuesLoadedCallback callback) {
        trace.start();
        venueList.clear();
        CollectionReference venuesCollection = mfirestore.collection("venues");
        CollectionReference categoriasCollection = mfirestore.collection("categorias");
        CollectionReference sector_venueCollection = mfirestore.collection("sector_venue");

        Task<QuerySnapshot> venuesTask = venuesCollection.get();
        Task<QuerySnapshot> categoriasTask = categoriasCollection.get();
        Task<QuerySnapshot> sectorVenueTask = sector_venueCollection.get();

        Tasks.whenAllComplete(venuesTask, categoriasTask, sectorVenueTask)
                .addOnCompleteListener(task -> {
                    if (venuesTask.isSuccessful() && categoriasTask.isSuccessful() && sectorVenueTask.isSuccessful()) {
                        trace.stop();
                        // Procesar los datos de venues
                        for (QueryDocumentSnapshot documentVenue : venuesTask.getResult()) {
                            Venue venue = documentVenue.toObject(Venue.class);
                            venueList.add(venue);
                        }

                        // Procesar las categorías
                        for (QueryDocumentSnapshot documentCategoria : categoriasTask.getResult()) {
                            Categoria categoria = documentCategoria.toObject(Categoria.class);
                            categoriaList.add(categoria);

                            int categoria_id = Objects.requireNonNull(documentCategoria.getLong("categoria_id")).intValue();
                            for (Venue venue : venueList) {
                                if (venue.getCategoria_id() == categoria_id) {
                                    venue.setCategoria(documentCategoria.getString("nombre"));
                                }
                                if (venue.getCategoria_id() == -1){
                                    venue.setCategoria("nulo");
                                }
                            }
                        }

                        //Procesar sector_venue y asignar a cada venue su sector correspondiente
                        for (QueryDocumentSnapshot documentSectorVenue : sectorVenueTask.getResult()) {
                            int venue_id = Objects.requireNonNull(documentSectorVenue.getLong("venue_id")).intValue();
                            for (Venue venue : venueList) {
                                if (venue.getId() == venue_id) {
                                    venue.setSector_id(Objects.requireNonNull(documentSectorVenue.getLong("sector_id")).intValue());
                                }
                            }
                        }

                        // Asignar iconos y sectores a cada venue en venueList
                        for (Venue venue : venueList) {
                            // Asignar icono basado en la categoría
                            for (VenueIconObj venueIcon : icons) {
                                if (venue.getCategoria() != null && venue.getCategoria().equals(venueIcon.getCategoryName())) {
                                    venue.setVenueIcon(venueIcon);
                                    Log.d("VenueProvider", "Icono asignado para categoría: " + venueIcon.getCategoryName());
                                }
                            }

                            // Asignar sector basado en sector_id
                            for (Sector sector : sectores) {
                                if (venue.getSector_id() == sector.getId()) {
                                    sector.setVenueId(venue.getId());
                                    venue.setSector(sector);
                                }
                            }
                        }

                        // Llamar al callback después de cargar todos los datos
                        callback.onVenuesLoaded(venueList);
                    } else {
                        trace.incrementMetric("error_count", 1);
                        trace.stop();
                        Log.e("VenueProvider", "Error al cargar algunos datos de Firestore");
                    }
                });

    }

    public interface OnVenuesLoadedCallback {
        void onVenuesLoaded(List<Venue> venues);
    }

}
