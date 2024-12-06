package com.proyecto.mallnav.ui.fragments;


import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.proyecto.mallnav.R;

import java.util.Objects;


public abstract class BaseFragment extends Fragment {
    private LocationManager locationManager  = null;
    private WifiManager wifiManager = null;
    private StateReceiver receiver = null;
    private IntentFilter  filter   = null;
    protected String wifiState = null;
    protected String geoLocationState = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSystemServices();
        initBroadcastReceiver();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            updateStatusBar();
            updateUiState();
            updateWarningMessageState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
        updateWifiState();
        updateGeolocationState();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    protected abstract void updateStatusBar();

    protected void updateUiState() { }

    protected void updateWarningMessageState() { }

    protected void onWifiStateChanged() {
        updateWifiState();
        updateWarningMessageState();
    }
    protected void onGpsStateChanged() {
        updateGeolocationState();
        updateWarningMessageState();
    }
    private void updateGeolocationState() {
        geoLocationState = isGpsEnabled() ? getString(R.string.state_on) :  getString(R.string.state_off);
    }
    private void updateWifiState() {
        wifiState = isWifiEnabled() ? getString(R.string.state_on) :  getString(R.string.state_off);
    }


    private void initSystemServices() {
        locationManager  = (LocationManager)  requireActivity().getSystemService(Context.LOCATION_SERVICE);
        wifiManager = (WifiManager) requireActivity().getSystemService(Context.WIFI_SERVICE);
    }

    private void initBroadcastReceiver() {
        receiver = new StateReceiver();
        filter   = new IntentFilter(WIFI_STATE_CHANGED_ACTION);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
    }

    private void registerReceiver() {
        requireActivity().registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        requireActivity().unregisterReceiver(receiver);
    }


    protected boolean isWifiEnabled() {
        if (wifiManager != null) {
            return  wifiManager.isWifiEnabled();
        }
        else
            return false;
    }
    protected boolean isGpsEnabled() {
        if (locationManager != null) {
            boolean isGpsEnabled     = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return  isGpsEnabled || isNetworkEnabled;
        }
        else
            return false;
    }

    private class StateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onGpsStateChanged();
                    break;
                case WIFI_STATE_CHANGED_ACTION:
                    onWifiStateChanged();
                    break;
            }
        }
    }

}
