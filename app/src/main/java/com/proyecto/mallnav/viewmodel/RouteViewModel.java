package com.proyecto.mallnav.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.mallnav.models.Nodo;
import com.proyecto.mallnav.models.Venue;

import java.util.List;

public class RouteViewModel extends ViewModel{
    private final MutableLiveData<Venue> pinnedVenueRoute = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRoutingEnabled = new MutableLiveData<>(false);

    public LiveData<Venue> getPinnedVenueRoute() {
        return pinnedVenueRoute;
    }
    public void setPinnedVenueRoute(Venue venue) {
        pinnedVenueRoute.setValue(venue);
    }

    public LiveData<Boolean> getIsRoutingEnabled() {
        return isRoutingEnabled;
    }
    public void setIsRoutingEnabled(boolean enabled) {
        isRoutingEnabled.setValue(enabled);
    }

}
