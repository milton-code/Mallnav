package com.proyecto.mallnav.utils;

import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.Sector;


import java.util.ArrayList;
import java.util.List;

public class SectorListProvider {

    public static List<Sector> SectorList = new ArrayList<>();
    private SectorListProvider() {}

    static {
        SectorList.add(new Sector(1, "Sector01",412,52,"B"));
        SectorList.add(new Sector(2, "Sector02",451,133,"D"));
        SectorList.add(new Sector(3, "Sector03",451,164,"I"));
        SectorList.add(new Sector(4, "Sector04",451,199,"O"));
        SectorList.add(new Sector(5, "Sector05",451,228,"Q"));
        SectorList.add(new Sector(6, "Sector06",451,249,""));
        SectorList.add(new Sector(7, "Sector07",412,330, ""));
        SectorList.add(new Sector(8, "Sector08",355,325, ""));
        SectorList.add(new Sector(9, "Sector09",335,384, ""));
        SectorList.add(new Sector(10, "Sector10",270,384, ""));
        SectorList.add(new Sector(11, "Sector11",204,384, ""));
        SectorList.add(new Sector(12, "Sector12",158,303, ""));
        SectorList.add(new Sector(13, "Sector13",129,270, ""));
        SectorList.add(new Sector(14, "Sector14",89,171, ""));
        SectorList.add(new Sector(15, "Sector15",188,223, ""));
        SectorList.add(new Sector(16, "Sector16",223,244, ""));
        SectorList.add(new Sector(17, "Sector17",258,244, ""));
        SectorList.add(new Sector(18, "Sector18",292,244, ""));
        SectorList.add(new Sector(19, "Sector19",331,244, ""));
        SectorList.add(new Sector(20, "Sector20",374,249, ""));
        SectorList.add(new Sector(21, "Sector21",374,228,"P"));
        SectorList.add(new Sector(22, "Sector22",374,199,"L"));
        SectorList.add(new Sector(23, "Sector23",374,164,"H"));
        SectorList.add(new Sector(24, "Sector24",374,109,"C"));
        SectorList.add(new Sector(25, "Sector25",204,285, ""));
        SectorList.add(new Sector(26, "Sector26",252,308, ""));
        SectorList.add(new Sector(27, "Sector27",289,308, ""));
        SectorList.add(new Sector(28, "Sector28",270,337, ""));
        SectorList.add(new Sector(29, "Sector29",412,280, ""));
        SectorList.add(new Sector(30, "Sector30",412,237, ""));
        SectorList.add(new Sector(31, "Sector31",412,122, ""));
    }
}
