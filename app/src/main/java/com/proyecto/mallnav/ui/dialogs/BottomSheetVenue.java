package com.proyecto.mallnav.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.proyecto.mallnav.R;


public class BottomSheetVenue extends BottomSheetDialogFragment {

    private TextView         mSheetTitle        = null;
    private TextView         mVenueCategory  = null;
    private ImageView        mVenueIcon = null;
    private MaterialButton   mCloseButton       = null;
    private MaterialButton   mRouteButton       = null;
    private MaterialButton   mAgregarVenue       = null;
    private MaterialButton   mActualizarVenue       = null;
    private MaterialButton   mEliminarVenue       = null;

    private String mTitle       = null;
    private String mCategory = null;
    private int mIcon = -1;
    private View.OnClickListener onClickRouteListener = null;
    private View.OnClickListener onClickAddVenueListener = null;
    private View.OnClickListener onClickUpdateVenueListener = null;
    private View.OnClickListener onClickDeleteVenueListener = null;
    private static int ROUTE_VISIBILITY = View.GONE;
    private static int ADD_VENUE_VISIBILITY = View.GONE;
    private static int UPDATE_VENUE_VISIBILITY = View.GONE;
    private static int DELETE_VENUE_VISIBILITY = View.GONE;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_bottom_venue, container, false);
        initViews(view);
        setViewsParams();
        setViewsListeners();

        return view;
    }

    private void initViews(View view) {
        mSheetTitle       = view.findViewById(R.id.venue_dialog__title);
        mVenueCategory = view.findViewById(R.id.venue_dialog__category);
        mVenueIcon = view.findViewById(R.id.venue_dialog__icon);
        mCloseButton      = view.findViewById(R.id.venue_dialog__search_btn_close);
        mRouteButton      = view.findViewById(R.id.venue_dialog__route_button);
        mAgregarVenue = view.findViewById(R.id.venue_dialog__add_venue_button);
        mActualizarVenue = view.findViewById(R.id.venue_dialog__update_venue_button);
        mEliminarVenue = view.findViewById(R.id.venue_dialog__delete_venue_button);
    }

    private void setViewsParams() {
        mSheetTitle.setText(mTitle);
        mVenueCategory.setText(mCategory);
        mVenueIcon.setImageResource(mIcon);

        mRouteButton.setVisibility(ROUTE_VISIBILITY);
        mRouteButton.setOnClickListener(onClickRouteListener);
        mAgregarVenue.setVisibility(ADD_VENUE_VISIBILITY);
        mAgregarVenue.setOnClickListener(onClickAddVenueListener);
        mActualizarVenue.setVisibility(UPDATE_VENUE_VISIBILITY);
        mActualizarVenue.setOnClickListener(onClickUpdateVenueListener);
        mEliminarVenue.setVisibility(DELETE_VENUE_VISIBILITY);
        mEliminarVenue.setOnClickListener(onClickDeleteVenueListener);
    }

    private void setViewsListeners() {
        mCloseButton.setOnClickListener(v -> dismiss());
    }

    public void setSheetTitle(String title) {
        mTitle = title;
    }
    public void setCategory(String categoria) {
        mCategory = categoria;
    }
    public void setIcon(int venuIcon) {
        mIcon = venuIcon;
    }

    public void setRouteButtonClick(View.OnClickListener listener) {
        onClickRouteListener = listener;
    }
    public void setRouteButtonVisibility(int visibility) {
        ROUTE_VISIBILITY = visibility;
    }

    public void setAgregarVenueButtonClick(View.OnClickListener listener) {
        onClickAddVenueListener = listener;
    }
    public void setAgregarVenueButtonVisibility(int visibility) {
        ADD_VENUE_VISIBILITY = visibility;
    }

    public void setActualizarVenueButtonClick(View.OnClickListener listener) {
        onClickUpdateVenueListener = listener;
    }
    public void setActualizarVenueButtonVisibility(int visibility) {
        UPDATE_VENUE_VISIBILITY = visibility;
    }

    public void setEliminarVenueButtonClick(View.OnClickListener listener) {
        onClickDeleteVenueListener = listener;
    }
    public void setEliminarVenueButtonVisibility(int visibility) {
        DELETE_VENUE_VISIBILITY = visibility;
    }

}