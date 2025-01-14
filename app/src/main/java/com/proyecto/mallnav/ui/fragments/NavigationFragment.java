package com.proyecto.mallnav.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.proyecto.mallnav.utils.Constants.KEY_FINGERPRINT;
import static com.proyecto.mallnav.utils.Constants.KEY_GEOLOCALIZAR;
import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_NAME;

import com.github.chrisbanes.photoview.PhotoView;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.textview.MaterialTextView;


import com.proyecto.mallnav.R;

import com.proyecto.mallnav.adapters.venues.VenueListAdapter;
import com.proyecto.mallnav.models.Nodo;
import com.proyecto.mallnav.models.Sector;
import com.proyecto.mallnav.models.Venue;
import com.proyecto.mallnav.models.VenueIconObj;
import com.proyecto.mallnav.service.WifiScanService;
import com.proyecto.mallnav.ui.dialogs.BottomSheetVenue;
import com.proyecto.mallnav.utils.CuadriculaListProvider;
import com.proyecto.mallnav.utils.KeyboardController;
import com.proyecto.mallnav.utils.PathMaker;
import com.proyecto.mallnav.utils.VenueProvider;
import com.proyecto.mallnav.viewmodel.NavigationViewModel;
import com.proyecto.mallnav.viewmodel.RouteViewModel;
import com.proyecto.mallnav.viewmodel.VenueViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NavigationFragment extends BaseFragment {
    //ViewModels
    private VenueViewModel venueViewModel;
    private NavigationViewModel navigationViewModel;
    private RouteViewModel routeViewModel;
    //Venues//
    private ImageView venueIcon = null;
    private List<ImageView> venueMapIconList = new ArrayList<>();
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
    private float density = -1.0f;
    private float posX = -1f;
    private float posY = -1f;
    private List<Nodo> listaNodos = null;
    private ConstraintLayout mNavigationLayout = null;
    private FrameLayout mNavigationMapContainer = null;
    private FrameLayout mNavigationMapContainerIcons = null;
    private FrameLayout mTransparentBackground = null;
    private FrameLayout mAdjustModeButton = null;
    private FrameLayout mFingerprintButton = null;
    private ImageView currPos = null;
    private CardView rutaCardView = null;
    private MaterialButton rutaCloseButton = null;
    private TextView destinoRuta = null;
    private TextView guiaRuta = null;
    private List<Nodo> ruta = Collections.emptyList();
    private PathMaker.PathOverlay rutaOverlay = null;
    private MaterialDividerItemDecoration mItemDivider = null;
    //MapView
    private PhotoView mapView = null;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        density = displayMetrics.density;
        Log.d("Density", "Screen density: " + density);
        CuadriculaListProvider.cargarCuadriculas(requireContext(), "mediciones.json");
        initViewModels();
        initReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        initViews(view);
        setViewsParams();
        initAdapters();
        setAdapters();
        setViewsListeners();
        listaNodos = CuadriculaListProvider.obtenerListaNodos();
        setObservers();
        updateWarningMessageState();
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

        if (!isWifiEnabled() && !isGpsEnabled()){
            showWarning(getString(R.string.err_navigation_state_gps_wifi));
            return;
        }

        if (!isWifiEnabled()){
            showWarning(getString(R.string.err_navigation_state_wifi));
            return;
        }

        if (!isGpsEnabled()){
            showWarning(getString(R.string.err_navigation_state_gps));
            return;
        }

        hideWarning();
    }

    private void showWarning(String message) {
        mWarningMessage.setText(message);
        mWarningMessage.setVisibility(VISIBLE);
    }

    private void hideWarning() {
        mWarningMessage.setVisibility(GONE);
    }

    @Override
    protected void updateStatusBar() {
        window.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.colorOnBackground));
    }

    private void initViewModels() {
        venueViewModel = new ViewModelProvider(this).get(VenueViewModel.class);
        navigationViewModel = new ViewModelProvider(requireActivity()).get(NavigationViewModel.class);
        routeViewModel = new ViewModelProvider(requireActivity()).get(RouteViewModel.class);
        WifiScanService.setViewModel(navigationViewModel);
    }

    private void initReceiver() {
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
        mNavigationMapContainerIcons = view.findViewById(R.id.navigation_map_container_icons);
        mapView = view.findViewById(R.id.navigation__map_view);
        currPos = view.findViewById(R.id.current_position);
        rutaCardView = view.findViewById(R.id.rutaCardView);
        rutaCloseButton = view.findViewById(R.id.ruta_btn_close);
        destinoRuta = view.findViewById(R.id.destinoRuta);
        guiaRuta = view.findViewById(R.id.guiaRuta);
        mSearchLayout = view.findViewById(R.id.navigation_search);
        mTransparentBackground = view.findViewById(R.id.navigation__search_transparent_bg);
        mVenueListLayout = view.findViewById(R.id.navigation__venue_listview);
        mSearchBtnClose = view.findViewById(R.id.navigation__search_btn_close);
        mSearchField = view.findViewById(R.id.navigation__search_field);
        mAdjustModeButton = view.findViewById(R.id.navigation__adjust_mode_button);
        mFingerprintButton = view.findViewById(R.id.wifi__fingerprinting_mode_button);
        mWarningMessage = view.findViewById(R.id.navigation__warning);
        mVenueListView = view.findViewById(R.id.recycler_list_venues);
        mItemDivider = new MaterialDividerItemDecoration(requireActivity(), MaterialDividerItemDecoration.VERTICAL);
        mVenueBottomSheet = new BottomSheetVenue();
    }

    private void setViewsParams(){
        mNavigationLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mapView.setImageResource(R.drawable.mapa_mall);
        mapView.setMaximumScale(10.0f);
        mapView.setMinimumScale(1.0f);
        mSearchLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mVenueListLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mItemDivider.setDividerColor(ContextCompat.getColor(requireActivity(), R.color.colorBackground));
        mItemDivider.setLastItemDecorated(false);
        mVenueListView.addItemDecoration(mItemDivider);
        currPos.setImageResource(R.drawable.ic_current_point);
        currPos.setZ(1f);
    }


    private void setViewsListeners() {

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

        rutaCloseButton.setOnClickListener(v -> {
            routeViewModel.setIsRoutingEnabled(false);
            rutaCardView.setVisibility(GONE);
            mTransparentBackground.setVisibility(VISIBLE);
            mNavigationMapContainer.removeView(rutaOverlay);
        });
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(!venueViewModel.getVenuesLiveData().isInitialized()) {
                    venueViewModel.loadVenues();
                }
                else {
                    applyVenueIconTransformations();
                }
                Log.d("venuesLiveData","La venuesLiveData está inicializada:"+venueViewModel.getVenuesLiveData().isInitialized());
            }
        });

        mapView.setOnMatrixChangeListener(rect -> {
            applyVenueIconTransformations();
            actualizarCurrPos(posX, posY);
            ajustarNodosRuta(ruta);
        });

        // Configurar el Listener
        mAdjustModeButton.setOnClickListener(v -> {
            boolean isEnabled = navigationViewModel.getIsScanningEnabled().getValue() != null
                    && navigationViewModel.getIsScanningEnabled().getValue();
            if(isWifiEnabled() && isGpsEnabled()) {
                navigationViewModel.setIsScanningEnabled(!isEnabled);

                if (!isEnabled) {
                    startGeolocation();

                } else {
                    stopGeolocation();
                }
            }
        });

        mFingerprintButton.setOnClickListener(v -> {
            if(isWifiEnabled() && isGpsEnabled()) {
                Intent intent = new Intent(requireContext(), WifiScanService.class);
                intent.setAction(KEY_FINGERPRINT);
                requireContext().startService(intent);
                Toast.makeText(requireContext(),"Fingerprinting activado",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(),"No se pudo activar el Fingerprinting",Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener listenerRuta = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(WifiScanService.nearestCuad != null) {
                    mVenueBottomSheet.dismiss();
                    routeViewModel.setPinnedVenueRoute(mPinVenue);
                    routeViewModel.setIsRoutingEnabled(true);
                }
                else {
                    Log.e("nearestCuad","El nearestCuad es null");
                }
            }
        };

        mVenueBottomSheet.setRouteButtonClick(listenerRuta);
    }

    public void establecerRuta(Nodo nodoStart) {
        PathMaker pathMaker = new PathMaker(listaNodos, density);
        String nodoEndId = Objects.requireNonNull(routeViewModel.getPinnedVenueRoute().getValue()).getSector().getNodoId();
        if(Objects.equals(nodoStart.getId(), nodoEndId)){
            routeViewModel.setIsRoutingEnabled(false);
            rutaCardView.setVisibility(GONE);
            mTransparentBackground.setVisibility(VISIBLE);
            mNavigationMapContainer.removeView(rutaOverlay);
            Toast.makeText(getContext(),"Usted ha llegado al destino", Toast.LENGTH_SHORT).show();
        }
        else {
            Nodo nodoEnd = listaNodos.stream().filter(nodo -> nodo.getId().equals(nodoEndId)).findFirst().orElse(null);
            ruta = pathMaker.crearRutaMasCorta(nodoStart, nodoEnd);////
            rutaOverlay = pathMaker.crearRutaOverlay(getContext());
            rutaOverlay.setZ(0f);
            mNavigationMapContainer.addView(rutaOverlay);
            ajustarNodosRuta(ruta);
            mTransparentBackground.setVisibility(GONE);
            destinoRuta.setText(routeViewModel.getPinnedVenueRoute().getValue().getNombre());
            guiaRuta.setText(getGuia(ruta));
            rutaCardView.setVisibility(VISIBLE);
        }

    }
    public String getGuia(List<Nodo> ruta){
        if(ruta.size() > 1){
            String guiaX = null;
            String guiaY = null;
            float nodoStartX = ruta.get(0).getNodoX();
            float nodoNextX = ruta.get(1).getNodoX();
            float nodoStartY = ruta.get(0).getNodoY();
            float nodoNextY = ruta.get(1).getNodoY();
            if (nodoStartX < nodoNextX) {
                guiaX = "derecha";
            } else if (nodoStartX > nodoNextX) {
                guiaX = "izquierda";
            }
            if (nodoStartY < nodoNextY) {
                guiaY = "abajo";
            } else if (nodoStartY > nodoNextY) {
                guiaY = "arriba";
            }
            if (nodoStartX == nodoNextX) {
                return "Diríjase hacia " + guiaY;
            } else if (nodoStartY == nodoNextY){
                return "Diríjase hacia la " + guiaX;
            }
            return "Diríjase hacia " + guiaY + " y a la " + guiaX;
        }
        return "";
    }

    private void ajustarNodosRuta(List<Nodo> ruta) {
        if(!ruta.isEmpty()){
            List<Pair<Float, Float>> listaCoordRuta = new ArrayList<>();
            // Obtiene los valores de la matriz para aplicar escala y traslación
            float[] matrixValues = new float[9];
            mapView.getImageMatrix().getValues(matrixValues);
            float scaleFactorX = matrixValues[Matrix.MSCALE_X];
            float scaleFactorY = matrixValues[Matrix.MSCALE_Y];
            float transX = matrixValues[Matrix.MTRANS_X];
            float transY = matrixValues[Matrix.MTRANS_Y];

            for (Nodo nodo : ruta) {
                float nodoX = nodo.getNodoX();
                float nodoY = nodo.getNodoY();
                float adjustedX = (((nodoX + 1) * density) * scaleFactorX) + transX;
                float adjustedY = (((nodoY + 2.5f) * density) * scaleFactorY) + transY;
                Pair<Float, Float> coordRuta = new Pair<>(adjustedX, adjustedY);
                listaCoordRuta.add(coordRuta);
            }

            rutaOverlay.setRutaCoord(listaCoordRuta);
            rutaOverlay.invalidate();
        }
        else {
            Log.e("Ruta","La ruta de nodos está vacia");
        }
    }

    private void startGeolocation() {
        Intent intent = new Intent(requireContext(), WifiScanService.class);
        intent.setAction(KEY_GEOLOCALIZAR);
        requireContext().startService(intent);
        Toast.makeText(requireContext(),"Geolocalización activada",Toast.LENGTH_SHORT).show();
    }

    private void stopGeolocation() {
        Intent intent = new Intent(requireContext(), WifiScanService.class);
        requireContext().stopService(intent);
        currPos.setVisibility(View.GONE);
        routeViewModel.setIsRoutingEnabled(false);
        rutaCardView.setVisibility(GONE);
        mTransparentBackground.setVisibility(VISIBLE);
        mNavigationMapContainer.removeView(rutaOverlay);
        Toast.makeText(requireContext(),"Geolocalización desactivada",Toast.LENGTH_SHORT).show();
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
        if (Boolean.TRUE.equals(navigationViewModel.getIsScanningEnabled().getValue())) {
            mVenueBottomSheet.setRouteButtonVisibility(VISIBLE);
        }
        else {
            mVenueBottomSheet.setRouteButtonVisibility(GONE);
        }
        mVenueBottomSheet.setSheetTitle(titleText);
        mVenueBottomSheet.setCategory(venueCategory);
        mVenueBottomSheet.setIcon(venueIcon);
        mVenueBottomSheet.show(getParentFragmentManager(), null);
    }

    private void initAdapters(){
        mVenueListAdapter = new VenueListAdapter();
    }

    private void setAdapters(){
        mVenueListView.setAdapter(mVenueListAdapter);
    }

    private void setObservers(){

        // Observamos cambios en la lista de venues para actualizar la UI
        venueViewModel.getVenuesLiveData().observe(getViewLifecycleOwner(), venues -> {
            if (venues != null && !venues.isEmpty()) {
                mVenueListAdapter.clear();
                mVenueListAdapter.submit(venues);
                mVenueListAdapter.notifyDataSetChanged();
                addIconsToMapWhenReady(venues);
                Log.w("Observer", "se ejecutó el Observer");
            } else {
                Log.w("Observer", "No venues available to display");
            }
        });
        //Observamos cambios en el isScanninEnabled para actualizar la UI
        navigationViewModel.getIsScanningEnabled().observe(getViewLifecycleOwner(), isEnabled -> {
            mAdjustModeButton.setBackgroundResource(isEnabled ? R.drawable.bg_adjust_btn_active: R.drawable.bg_adjust_btn);
        });

        
        navigationViewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {

            if (position != null) {
                posX = position.getNodoX();
                posY = position.getNodoY();
                currPos.setVisibility(View.VISIBLE);
                actualizarCurrPos(posX, posY);
                if(Boolean.TRUE.equals(routeViewModel.getIsRoutingEnabled().getValue())){
                    mNavigationMapContainer.removeView(rutaOverlay);
                    establecerRuta(position);
                }
            }
            else {
                Log.e("ErrorPosicion: ", "la posicion es nula");
            }
        });
    }

    private void actualizarCurrPos(float posX, float posY) {
        // Lógica para posicionar el ícono en el mapa
        if (currPos.getVisibility() == GONE) {
            Log.d("Matrix Listener", "Current position no es visible");
            return;
        }
        // Obtiene los valores de la matriz para aplicar escala y traslación
        float[] matrixValues = new float[9];
        mapView.getImageMatrix().getValues(matrixValues);
        float scaleFactorX = matrixValues[Matrix.MSCALE_X];
        float scaleFactorY = matrixValues[Matrix.MSCALE_Y];
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];

        // Calcular la nueva posición ajustada del ícono según el zoom y el desplazamiento
        float adjustedX = (((posX + 1 - 2) * density) * scaleFactorX) + transX;
        Log.d("currPosUpdate","currPosX: " + posX);
        Log.d("currPosUpdate","adjustedX: "+adjustedX);
        float adjustedY = (((posY + 2.5f -2) * density) * scaleFactorY) + transY;
        Log.d("currPosUpdate","currPosY: "+ posY);
        Log.d("currPosUpdate","adjustedY: "+adjustedY);
        int adjustedWidth = Math.round(((4 * density) * scaleFactorX));
        int adjustedHeight = Math.round(((4 * density) * scaleFactorY));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adjustedWidth,adjustedHeight);
        currPos.setLayoutParams(layoutParams);
        currPos.setX(adjustedX);
        currPos.setY(adjustedY);
    }

    private void addIconsToMapWhenReady(List<Venue> venues) {
        venueMapIconList.clear();
        mNavigationMapContainerIcons.removeAllViews();
        // Sabemos que los venues ya están cargados y listos para usarse
        for (Venue venue : venues) {
                addMapVenueIcons(venue);
        }
        applyVenueIconTransformations();
    }

    private void addMapVenueIcons(Venue venue){
        if(venue.getCategoria_id() == -1){
            return;
        }
        VenueIconObj venueIconObj = venue.getVenueIcon();
        Sector sector = venue.getSector();
        if (venueIconObj == null || sector == null) {
            Log.w("NavigationFragment", "El venue con ID " + venue.getId() + " no tiene un icono o sector asignado, omitiendo.");
            return; // Omite si no hay icono asignado
        }

        venueIcon = new ImageView(requireContext());
        venueIcon.setImageResource(venue.getVenueIcon().getImageDrawable());
        int ogPointX = venue.getSector().getPointX() + 4;
        int ogPointY = venue.getSector().getPointY() + 4;

        venueIcon.setTag(R.id.venueIconWidth,ogPointX);
        venueIcon.setTag(R.id.venueIconHeight,ogPointY);
        venueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinVenue = venue;
                showVenueBottomSheet();
            }
        });

        mNavigationMapContainerIcons.addView(venueIcon);
        venueMapIconList.add(venueIcon);
    }

    private void applyVenueIconTransformations() {
        if (venueMapIconList.isEmpty()) {
            Log.d("Matrix Listener", "venueMapIconList no está listo");
            return;
        }

        // Obtiene los valores de la matriz para aplicar escala y traslación
        float[] matrixValues = new float[9];
        mapView.getImageMatrix().getValues(matrixValues);
        float scaleFactorX = matrixValues[Matrix.MSCALE_X];
        Log.d("Matrix Listener", "scaleFactorX: "+scaleFactorX);
        float scaleFactorY = matrixValues[Matrix.MSCALE_Y];
        Log.d("Matrix Listener", "scaleFactorY: "+scaleFactorY);
        float transX = matrixValues[Matrix.MTRANS_X];
        Log.d("Matrix Listener", "transX: "+transX);
        float transY = matrixValues[Matrix.MTRANS_Y];
        Log.d("Matrix Listener", "transY: "+transY);

        // Aplica la posición ajustada a cada ícono
        for (ImageView venueIcon : venueMapIconList) {
            updateVenueIconPosition(venueIcon, scaleFactorX, scaleFactorY, transX, transY);
        }

    }


    private void updateVenueIconPosition(ImageView venueIcon,float scaleFactorX, float scaleFactorY, float transX, float transY) {
        int ogwidth = (int) venueIcon.getTag(R.id.venueIconWidth);
        int ogHeight = (int) venueIcon.getTag(R.id.venueIconHeight);
        // Calcular la nueva posición ajustada del ícono según el zoom y el desplazamiento
        float adjustedX = ( (ogwidth * density) * scaleFactorX) + transX;
        Log.d("venueIconUpdate","venueIconXupdate: "+venueIcon.getX());
        Log.d("venueIconUpdate","adjustedX: "+adjustedX);
        float adjustedY = ((ogHeight * density) * scaleFactorY) + transY;
        Log.d("venueIconUpdate","venueIconYupdate: "+venueIcon.getY());
        Log.d("venueIconUpdate","adjustedY: "+adjustedY);
        int adjustedWidth = Math.round(((12* density) * scaleFactorX));
        int adjustedHeight = Math.round(((12* density) * scaleFactorY));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(adjustedWidth,adjustedHeight);
        venueIcon.setLayoutParams(layoutParams);
        venueIcon.setX(adjustedX);
        venueIcon.setY(adjustedY);
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
            } else {
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