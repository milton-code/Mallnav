package com.proyecto.mallnav.ui.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyecto.mallnav.ui.fragments.BaseFragment;
import com.navigine.idl.java.IconMapObject;
import com.navigine.idl.java.Location;
import com.navigine.idl.java.LocationListener;
import com.navigine.idl.java.LocationManager;
import com.navigine.idl.java.NavigineSdk;
import com.navigine.view.LocationView;
import com.proyecto.mallnav.R;
import com.proyecto.mallnav.utils.NavigineSdkManager;


public class NavigationFragment extends BaseFragment {
    LocationView mLocationView;
    LocationManager mLocationManager;
    IconMapObject mPositionIcon = null;
    NavigineSdk mNavigineSdk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void updateStatusBar() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        mLocationView = view.findViewById(R.id.location_view);
        mPositionIcon = mLocationView.getLocationWindow().addIconMapObject();
        mPositionIcon.setSize(30, 30);
        mPositionIcon.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_current_point));

        mLocationManager = mNavigineSdk.getLocationManager();
        mLocationManager.addLocationListener(new LocationListener() {
            @Override
            public void onLocationLoaded(Location location) {
                mLocationView.getLocationWindow().setSublocationId(location.getSublocations().get(0).getId());
            }

            @Override
            public void onLocationFailed(int i, Error error) {

            }

            @Override
            public void onLocationUploaded(int i) {

            }


        });
        return mLocationView;
    }
}