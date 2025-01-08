package com.proyecto.mallnav.utils;

import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.VenueIconObj;

import java.util.ArrayList;
import java.util.List;

public class VenueIconsListProvider {
    public static List<VenueIconObj> VenueIconsList = new ArrayList<>();
    private VenueIconsListProvider() {}

    static {
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_lift, "Ascensor"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_cosmetics, "Cosméticos"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_jewellery, "Joyería"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_toilet, "Baño"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_traveller_goods, "Viajes"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_general, "Variedades"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_escalator, "Escalera eléctrica"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_car_services, "Automóviles"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_atm_banks, "Banco"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_footwear, "Calzado"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_food, "Restaurante"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_homeware, "Hogar"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_pharmacies, "Farmacia"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_supermarket, "Supermercado"));
        VenueIconsList.add(new VenueIconObj(R.drawable.ic_venue_clothing, "Ropa"));
        VenueIconsList.add(new VenueIconObj(R.drawable.add_circle_24px, "nulo"));
    }

}
