package com.proyecto.mallnav.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_NAME;

import com.caverock.androidsvg.SVG;
import com.github.chrisbanes.photoview.PhotoView;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.textview.MaterialTextView;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.proyecto.mallnav.R;

import com.proyecto.mallnav.adapters.venues.VenueListAdapter;
import com.proyecto.mallnav.models.Venue;
import com.proyecto.mallnav.ui.activities.LoginActivity;
import com.proyecto.mallnav.ui.activities.MainActivity;
/*import com.proyecto.mallnav.ui.custom.lists.BottomSheetListView;
import com.proyecto.mallnav.ui.dialogs.sheets.BottomSheetVenue;
import com.proyecto.mallnav.utils.ColorUtils;
import com.proyecto.mallnav.utils.DimensionUtils;
import com.proyecto.mallnav.utils.NavigineSdkManager;
import com.proyecto.mallnav.utils.VenueIconsListProvider;
import com.proyecto.mallnav.viewmodel.NavigationViewModel;*/
import com.proyecto.mallnav.ui.dialogs.BottomSheetVenue;
import com.proyecto.mallnav.utils.KeyboardController;
import com.proyecto.mallnav.utils.VenueProvider;
import com.proyecto.mallnav.viewmodel.VenueViewModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationFragment extends BaseFragment {
    //ViewModels
    private VenueViewModel venueViewModel;
    //Venues//
    private Venue mPinVenue    = null;
    private FrameLayout mVenueListLayout = null;
    private VenueListAdapter mVenueListAdapter = null;
    private RecyclerView mVenueListView = null;
    private BottomSheetVenue mVenueBottomSheet = null;
    //Barra de busqueda
    private SearchView mSearchField = null;
    private MaterialButton mSearchBtnClose = null;
    private LinearLayout mSearchLayout = null;
    //Miscelaneas
    private Window window = null;
    private StateReceiver mStateReceiver = null;
    private IntentFilter mStateReceiverFilter = null;
    //Warnings
    private MaterialTextView mWarningMessage = null;
    //UI
    private ConstraintLayout mNavigationLayout = null;
    private FrameLayout mNavigationMapContainer = null;
    private FrameLayout mTransparentBackground = null;
    private MaterialDividerItemDecoration mItemDivider = null;
    //MapView
    private PhotoView mapView = null;
    //private static final FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        initBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        initViews(view);
        setViewsParams();
        getDimensions();
        initLocationViewObjects();
        initAdapters();
        setAdapters();
        setViewsListeners();
        setObservers();
        updateWarningMessageState();
        //mVenueListAdapter.submit(VenueProvider.venueList);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onHiddenChanged(!isVisible());
        addListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListeners();
    }

    @Override
    protected void updateWarningMessageState(){
        if (!isGpsEnabled() && !isBluetoothEnabled())
        {
            showWarning(getString(R.string.err_navigation_state_gps_bluetooth));
            return;
        }

        if (!isBluetoothEnabled())
        {
            showWarning(getString(R.string.err_navigation_state_bluetooth));
            return;
        }

        if (!isGpsEnabled())
        {
            showWarning(getString(R.string.err_navigation_state_gps));
            return;
        }

        hideWarning();
    }

    private void showWarning(String message) {
        mWarningMessage.setText(message);
        mWarningMessage.setVisibility(GONE);
    }

    private void hideWarning() {
        mWarningMessage.setVisibility(GONE);
    }

    @Override
    protected void updateStatusBar() {
        window.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.colorOnBackground));
    }

    @Override
    protected void updateUiState() {
        //if (mLocationChanged) showLoadProgress();
        //if (mLocationView != null) mLocationView.onStart();
    }

    private void initViewModels() {
        venueViewModel = new ViewModelProvider(this).get(VenueViewModel.class);
        //viewModel = new ViewModelProvider(requireActivity()).get(NavigationViewModel.class);
    }

    private void initBroadcastReceiver() {
        mStateReceiver = new StateReceiver();
        mStateReceiverFilter = new IntentFilter();
        mStateReceiverFilter.addAction(VENUE_SELECTED);
    }

    private void addListeners() {
        requireActivity().registerReceiver(mStateReceiver, mStateReceiverFilter);
    }

    private void removeListeners() {
        requireActivity().unregisterReceiver(mStateReceiver);
    }

    private void initViews(View view) {
        window = requireActivity().getWindow();
        mNavigationLayout = view.findViewById(R.id.navigation__navigation_layout);
        mNavigationMapContainer = view.findViewById(R.id.navigation_map_container);
        mapView = view.findViewById(R.id.navigation__map_view);
        //mVenueIconsLayout = view.findViewById(R.id.navigation__venue_icons);
        mSearchLayout = view.findViewById(R.id.navigation_search);
        mTransparentBackground = view.findViewById(R.id.navigation__search_transparent_bg);
        mVenueListLayout = view.findViewById(R.id.navigation__venue_listview);
        mSearchBtnClose = view.findViewById(R.id.navigation__search_btn_close);
        mSearchField = view.findViewById(R.id.navigation__search_field);
        mWarningMessage = view.findViewById(R.id.navigation__warning);
        mVenueListView = view.findViewById(R.id.recycler_list_venues);
        mItemDivider = new MaterialDividerItemDecoration(requireActivity(), MaterialDividerItemDecoration.VERTICAL);
        //mVenueIconsListView = view.findViewById(R.id.recycler_list_venue_icons);
        mVenueBottomSheet = new BottomSheetVenue();
    }

    private void setViewsParams(){
        mNavigationLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mapView.setImageResource(R.drawable.mapa_mall);
        mapView.setMaximumScale(10.0f);
        mapView.setMinimumScale(1.0f);
        mSearchLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mVenueListLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        //mLocationView.setBackgroundColor(Color.argb(255, 235, 235, 235));
        //mLocationView.getLocationWindow().setStickToBorder(true);
        mItemDivider.setDividerColor(ContextCompat.getColor(requireActivity(), R.color.colorBackground));
        mItemDivider.setLastItemDecorated(false);
        mVenueListView.addItemDecoration(mItemDivider);

        //mLocationView.getLocationWindow().setPickRadius(10);
    }

    private void getDimensions() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        Log.d("Density", "Screen density: " + density);
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Obtener las dimensiones del drawable (imagen) dentro del PhotoView
                Drawable drawable = mapView.getDrawable();
                if (drawable != null) {
                    int imageWidth = drawable.getIntrinsicWidth();
                    int imageHeight = drawable.getIntrinsicHeight();
                    Log.d("ImageDimensions", "Image width: " + imageWidth + ", Image height: " + imageHeight);
                }

                // Obtener las dimensiones del photoview
                int photoViewWidth = mapView.getWidth();
                int photoViewHeight = mapView.getHeight();
                Log.d("PhotoViewDimensions", "PhotoView width: " + photoViewWidth + ", PhotoView height: " + photoViewHeight);

                float[] matrixValues = new float[9];
                mapView.getImageMatrix().getValues(matrixValues);
                // La escala aplicada a la imagen (tanto en X como en Y)
                float scaleFactorX = matrixValues[Matrix.MSCALE_X];
                float scaleFactorY = matrixValues[Matrix.MSCALE_Y];
                Log.d("EscalasAplicadas", "width: " + scaleFactorX + ", height: " + scaleFactorY);

                // Tamaño ajustado de la imagen
                int adjustedWidth = Math.round(drawable.getIntrinsicWidth() * scaleFactorX);
                int adjustedHeight = Math.round(drawable.getIntrinsicHeight() * scaleFactorY);
                Log.d("AdjustedDimensions", "Adjusted width: " + adjustedWidth + ", Adjusted height: " + adjustedHeight);

                // Remover el listener para evitar que se ejecute varias veces
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int pointStartY = (photoViewHeight - adjustedHeight)/2;
                float normalizerX = density*scaleFactorX;
                float normalizerY = density*scaleFactorY;
                int venuePointX = -1;
                int venuePointY = -1;

                for(Venue venue : VenueProvider.venueList){
                    if(venue.getSector_id()==1){
                        venuePointX = venue.getSector().getPointX();
                        venuePointY = venue.getSector().getPointY();
                    }
                }
                float pointX =  venuePointX*normalizerX;
                float pointY =  pointStartY+(venuePointY*normalizerY);
                addVenueIcons(pointX,pointY,normalizerX,normalizerY);
            }
        });

          // Posición Y relativa

        // Agrega el listener para abrir el BottomSheet cuando se haga clic en el ícono
        /*venueIcon.setOnClickListener(v -> {
            mPinVenue = venue;
            showVenueBottomSheet();
        });*/

        // Añadir el ícono al contenedor del mapa

    }

    private void addVenueIcons(float pointX, float pointY, float normalizerX, float normalizerY){
        ImageView venueIcon = new ImageView(requireContext());
        venueIcon.setImageResource(R.drawable.ic_venue_clothing);  // Usar el icono correspondiente
        float venueIconWidth = 20*normalizerX;
        float venueIconHeight = 20*normalizerY;
        venueIcon.setLayoutParams(new FrameLayout.LayoutParams((int)venueIconWidth,(int)venueIconHeight));  // Tamaño del ícono
        venueIcon.setX(pointX);
        venueIcon.setY(pointY);
        mNavigationMapContainer.addView(venueIcon);
    }

    private void setViewsListeners(){

        mTransparentBackground.setOnClickListener(v -> onHandleCancelSearch());

        mSearchField.setOnQueryTextFocusChangeListener(this::onSearchBoxFocusChange);

        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onHandleSearchQueryChange(newText);
                return true;
            }
        });

        mSearchBtnClose.setOnClickListener(v -> onHandleCancelSearch());

    }

    @SuppressLint("UseRequireInsteadOfGet")
    private void showVenueBottomSheet(){
        String titleText = mPinVenue.getNombre();
        String venueCategory = mPinVenue.getCategoria();
        int venueIcon = mPinVenue.getVenueIcon().getImageDrawable();
        Log.d("categoria",venueCategory);
        if (titleText.length() > 25) {
            titleText = titleText.substring(0, 24) + "…";
        }

        mVenueBottomSheet.setSheetTitle(titleText);
        mVenueBottomSheet.setCategory(venueCategory);
        mVenueBottomSheet.setIcon(venueIcon);
        mVenueBottomSheet.setRouteButtonVisibility(VISIBLE);

        mVenueBottomSheet.show(getParentFragmentManager(), null);
    }


    private void initLocationViewObjects() { }

    private void initAdapters(){
        mVenueListAdapter = new VenueListAdapter();
        //mVenuesIconsListAdapter = new VenuesIconsListAdapter();
    }

    private void setAdapters(){
        mVenueListView.setAdapter(mVenueListAdapter);
        //mVenueIconsListView.setAdapter(mVenuesIconsListAdapter);
    }

    private void setObservers(){
        // Observamos cambios en la lista de venues para actualizar la UI
        venueViewModel.getVenuesLiveData().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null && !venues.isEmpty()){
                mVenueListAdapter.clear();
                mVenueListAdapter.submit(venues);
                mVenueListAdapter.notifyDataSetChanged();
            } else {
                Log.w("NavigationFragment", "No venues available to display");
            }
        });
        venueViewModel.loadVenues();
    }


    private void onHandleCancelSearch() {
        onCloseSearch();
        hideTransparentLayout();
    }

    private void showTransparentLayout() {
        mTransparentBackground.setBackground(ContextCompat.getDrawable(requireActivity(), android.R.drawable.screen_background_dark_transparent));
        mTransparentBackground.setClickable(true);
        mTransparentBackground.setFocusable(true);
    }

    private void hideTransparentLayout() {
        mTransparentBackground.setBackground(null);
        mTransparentBackground.setClickable(false);
        mTransparentBackground.setFocusable(false);
    }

    private void onCloseSearch() {
        changeSearchLayoutBackground(Color.TRANSPARENT);
        mSearchField.clearFocus();
        mSearchBtnClose.setVisibility(GONE);
        hideVenueLayouts();
    }

    private void changeSearchLayoutBackground(int color) {
        mSearchLayout.getBackground().setTint(color);
    }

    private void onHandleSearchQueryChange(String query) {
        if (query.isEmpty()) {

            showSearchCLoseBtn();
            hideVenueListLayout();

        } else {
            hideSearchCLoseBtn();

            showVenueListLayout();
        }
        filterVenueListByQuery(query);
    }

    private void filterVenueListByQuery(String query) {
        mVenueListAdapter.filter(query);
    }

    private void hideVenueLayouts() {
        mVenueListLayout.setVisibility(GONE);
    }

    private void onSearchBoxFocusChange(View v, boolean hasFocus) {
        boolean isQueryEmpty = ((SearchView) v).getQuery().toString().isEmpty();
        if (hasFocus) {
            showTransparentLayout();
            changeSearchLayoutBackground(Color.WHITE);
            changeSearchBoxStroke(R.color.colorPrimary);
            showSearchCLoseBtn();
            if (isQueryEmpty) {
                hideVenueListLayout();
                //showVenueIconsLayout();
                //populateVenueIconsLayout();
            } else {
                //hideVenueIconsLayout();
                showVenueListLayout();
            }
        } else {
            changeSearchBoxStroke(R.color.colorSecondary);
            KeyboardController.hideSoftKeyboard(requireActivity());
        }
    }

    private void changeSearchBoxStroke(int color) {
        ((GradientDrawable) mSearchField.getBackground()).setStroke((int) getContext().getResources().getDimension(R.dimen.search_stroke_width), color);
    }


    private void showVenueListLayout() {
        mVenueListLayout.setVisibility(VISIBLE);
    }

    private void hideVenueListLayout() {
        mVenueListLayout.setVisibility(GONE);
    }


    private void hideSearchCLoseBtn() {
        mSearchBtnClose.setVisibility(GONE);
    }

    private void showSearchCLoseBtn() {
        mSearchBtnClose.setVisibility(VISIBLE);
    }


    private class StateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && VENUE_SELECTED.equals(intent.getAction())) {
                String venueName = intent.getStringExtra(KEY_VENUE_NAME);
                onHandleCancelSearch();
                for (int i = 0; i < VenueProvider.venueList.size(); i++) {
                    Venue v = VenueProvider.venueList.get(i);
                    if (v.getNombre().equals(venueName)) {
                        mPinVenue = v;
                        showVenueBottomSheet();
                        break;
                    }
                }
            }
        }
    }
}