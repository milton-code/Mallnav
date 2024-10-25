package com.proyecto.mallnav.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.mallnav.models.Venue;
import com.proyecto.mallnav.utils.VenueProvider;

import java.util.List;

public class VenueViewModel extends ViewModel {
    private MutableLiveData<List<Venue>> venuesLiveData = new MutableLiveData<>();

    public LiveData<List<Venue>> getVenuesLiveData() {
        return venuesLiveData;
    }

    public void loadVenues() {
        VenueProvider.initVenues(venues -> venuesLiveData.setValue(venues));
    }
}
