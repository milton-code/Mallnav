package com.proyecto.mallnav.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.perf.FirebasePerformance;

public class MallnavApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
        // Inicializar Firebase Performance
        FirebasePerformance.getInstance().setPerformanceCollectionEnabled(true);
    }
}
