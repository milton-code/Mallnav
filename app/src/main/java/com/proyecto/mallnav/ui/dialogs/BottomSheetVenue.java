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

    private String mTitle       = null;
    private String mCategory = null;
    private int mIcon = -1;

    private View.OnClickListener onClickListener = null;
    private static int VISIBILITY = View.VISIBLE;


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
    }

    private void setViewsParams() {
        mSheetTitle.setText(mTitle);
        mVenueCategory.setText(mCategory);
        mRouteButton.setVisibility(VISIBILITY);
        mVenueIcon.setImageResource(mIcon);
        mRouteButton.setOnClickListener(onClickListener);
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
        onClickListener = listener;
    }
    public void setRouteButtonVisibility(int visibility) {
        VISIBILITY = visibility;
    }

}