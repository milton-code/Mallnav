package com.proyecto.mallnav.viewmodel;

import android.util.Pair;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.mallnav.models.Nodo;

import java.util.List;

public class NavigationViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isScanningEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Pair<Float, Float>> currentPosition = new MutableLiveData<>();
    private final MutableLiveData<List<Nodo>> rutaActual = new MutableLiveData<>();

    public LiveData<Boolean> getIsScanningEnabled() {
        return isScanningEnabled;
    }
    public void setIsScanningEnabled(boolean enabled) {
        isScanningEnabled.setValue(enabled);
    }

    public LiveData<Pair<Float, Float>> getCurrentPosition() {
        return currentPosition;
    }
    public void updatePosition(Float x, Float y) {
        currentPosition.setValue(new Pair<>(x, y));
    }

    
    public LiveData<List<Nodo>> getRutaActual() {
        return rutaActual;
    }
    public void actualizarRuta(List<Nodo> nuevaRuta) {
        rutaActual.setValue(nuevaRuta);
    }
}
