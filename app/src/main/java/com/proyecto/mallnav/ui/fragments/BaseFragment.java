package com.proyecto.mallnav.ui.fragments;

//import static com.proyecto.mallnav.utils.Constants.LOCATION_CHANGED;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.proyecto.mallnav.R;


public abstract class BaseFragment extends Fragment {

    private LocationManager locationManager  = null;
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private StateReceiver receiver = null;
    private IntentFilter  filter   = null;
    protected String bluetoothState   = null;
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
        updateGeolocationState();
        updateBluetoothState();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    protected abstract void updateStatusBar();

    protected void updateUiState() { }

    protected void updateWarningMessageState() { }

    protected void onGpsStateChanged() {
        updateGeolocationState();
        updateWarningMessageState();
    }

    protected void onBluetoothStateChanged() {
        updateBluetoothState();
        updateWarningMessageState();
    }

    private void updateGeolocationState() {
        geoLocationState = isGpsEnabled() ? getString(R.string.state_on) :  getString(R.string.state_off);
    }

    private void updateBluetoothState() {
        bluetoothState = isBluetoothEnabled() ? getString(R.string.state_on) : getString(R.string.state_off);
    }

    private void initSystemServices() {
        locationManager  = (LocationManager)  requireActivity().getSystemService(Context.LOCATION_SERVICE);
        bluetoothManager = (BluetoothManager) requireActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void initBroadcastReceiver() {
        receiver = new StateReceiver();
        filter   = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //filter.addAction(LOCATION_CHANGED);
    }

    private void registerReceiver() {
        requireActivity().registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        requireActivity().unregisterReceiver(receiver);
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

    protected boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    /*protected void openLocationsScreen() {
        mNavigationView.setSelectedItemId(R.id.navigation_locations);
    }*/

    private class StateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onGpsStateChanged();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    onBluetoothStateChanged();
                    break;
            }
        }
    }

}
