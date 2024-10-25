package com.proyecto.mallnav.utils;

import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.Sector;


import java.util.ArrayList;
import java.util.List;

public class SectorListProvider {

    public static List<Sector> SectorList = new ArrayList<>();
    private SectorListProvider() {}

    static {
        SectorList.add(new Sector(1, "Sector01",414,52));
    }
}
