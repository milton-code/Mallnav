package com.proyecto.mallnav.viewmodel;

import android.util.Pair;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.mallnav.models.Nodo;
import com.proyecto.mallnav.models.Venue;

import java.util.List;

public class NavigationViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isScanningEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Nodo> currentPosition = new MutableLiveData<>();


    public LiveData<Boolean> getIsScanningEnabled() {
        return isScanningEnabled;
    }
    public void setIsScanningEnabled(boolean enabled) {
        isScanningEnabled.setValue(enabled);
    }

    public LiveData<Nodo> getCurrentPosition() {
        return currentPosition;
    }
    public void updatePosition(Nodo updatedPosition) {
        currentPosition.setValue(updatedPosition);
    }

}
