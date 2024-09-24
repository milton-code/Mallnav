package com.proyecto.mallnav.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
/*import static com.proyecto.mallnav.service.NavigationService.ACTION_POSITION_ERROR;
import static com.proyecto.mallnav.service.NavigationService.ACTION_POSITION_UPDATED;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_CATEGORY;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_NAME;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_POINT;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_SUBLOCATION;
import static com.proyecto.mallnav.utils.Constants.VENUE_FILTER_OFF;
import static com.proyecto.mallnav.utils.Constants.VENUE_FILTER_ON;
import static com.proyecto.mallnav.utils.Constants.TAG;
import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;*/

import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

//import com.proyecto.mallnav.service.NavigationService;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.proyecto.mallnav.R;

import com.proyecto.mallnav.adapters.venues.VenueListAdapter;
import com.proyecto.mallnav.models.Venue;
import com.proyecto.mallnav.ui.activities.LoginActivity;
import com.proyecto.mallnav.ui.activities.MainActivity;
/*import com.proyecto.mallnav.ui.custom.lists.BottomSheetListView;
import com.proyecto.mallnav.ui.dialogs.sheets.BottomSheetVenue;
import com.proyecto.mallnav.utils.ColorUtils;
import com.proyecto.mallnav.utils.DimensionUtils;
import com.proyecto.mallnav.utils.KeyboardController;
import com.proyecto.mallnav.utils.NavigineSdkManager;
import com.proyecto.mallnav.utils.VenueIconsListProvider;
import com.proyecto.mallnav.viewmodel.NavigationViewModel;*/
import com.proyecto.mallnav.utils.KeyboardController;
import com.proyecto.mallnav.utils.VenueProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationFragment extends BaseFragment {
    private float mZoomCameraDefault = 0f;
    private Window window = null;
    private MaterialButton mSearchBtnClose = null;
    private SearchView mSearchField = null;
    private BottomSheetBehavior mMakeRouteBehavior = null;
    private BottomSheetBehavior mCancelRouteBehaviour = null;

    private ConstraintLayout mNavigationLayout = null;
    private ConstraintLayout mMakeRouteSheet = null;
    private LinearLayout mSearchLayout = null;
    private FrameLayout mTransparentBackground = null;
    private VenueListAdapter mVenueListAdapter = null;
    private FrameLayout mVenueListLayout = null;
    private FrameLayout mVenueIconsLayout = null;
    private FrameLayout mAdjustModeButton = null;
    private MaterialTextView mFromCurrentText = null;
    private MaterialTextView mToText = null;
    private MaterialTextView mCancelRouteDistance = null;
    private MaterialTextView mCancelRouteTime = null;
    private MaterialTextView mWarningMessage = null;
    private MaterialTextView mDelayMessage = null;
    private ImageView mFromImageView = null;
    private MaterialButton mStartRouteButton = null;
    private MaterialButton mRouteSheetCancelButton    = null;

    private MaterialDividerItemDecoration mItemDivider = null;

    private StateReceiver mStateReceiver = null;
    //private PositionReceiver mPositionReceiver = null;

    private IntentFilter mStateReceiverFilter = null;
    private IntentFilter mPositionReceiverFilter = null;

    private RecyclerView mVenueListView = null;
    private RecyclerView mVenueIconsListView = null;

    private boolean mLocationLoaded = false;
    private boolean mAdjustMode = false;
    private boolean mRouting = false;
    private boolean mSelectMapPoint = false;
    private boolean mSetupPosition = true;
    private boolean mOrientationPointState  = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VenueProvider.initVenues(getContext());
        initViewModels();
        //initListeners();
        initBroadcastReceiver();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        initViews(view);
        setViewsParams();
        initLocationViewObjects();
        initAdapters();
        setAdapters();
        setViewsListeners();
        setObservers();
        updateWarningMessageState();
        mVenueListAdapter.submit(VenueProvider.venueList);

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
        //mLocationView.onStop();
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
        mWarningMessage.setVisibility(VISIBLE);
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
        if (mLocationLoaded)  loadMap();
        //if (mLocationView != null) mLocationView.onStart();
    }

    private void initViewModels() {
        //viewModel = new ViewModelProvider(requireActivity()).get(NavigationViewModel.class);
    }

    private void initBroadcastReceiver() {
        mStateReceiver = new StateReceiver();
        //mPositionReceiver = new PositionReceiver();

        mStateReceiverFilter = new IntentFilter();
        //mPositionReceiverFilter = new IntentFilter();

        //mStateReceiverFilter.addAction(LOCATION_CHANGED);
        mStateReceiverFilter.addAction(VENUE_SELECTED);
        //mStateReceiverFilter.addAction(VENUE_FILTER_ON);
        //mStateReceiverFilter.addAction(VENUE_FILTER_OFF);

        //mPositionReceiverFilter.addAction(ACTION_POSITION_UPDATED);
        //mPositionReceiverFilter.addAction(ACTION_POSITION_ERROR);
    }

    private void addListeners() {
        requireActivity().registerReceiver(mStateReceiver, mStateReceiverFilter);
        //requireActivity().registerReceiver(mPositionReceiver, mPositionReceiverFilter);

        //NavigineSdkManager.RouteManager.addRouteListener(mRouteListener);
    }

    private void removeListeners() {
        requireActivity().unregisterReceiver(mStateReceiver);
        //requireActivity().unregisterReceiver(mPositionReceiver);

        //NavigineSdkManager.RouteManager.removeRouteListener(mRouteListener);
    }

    private void initViews(View view) {
        window = requireActivity().getWindow();
        mNavigationLayout = view.findViewById(R.id.navigation__navigation_layout);
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
        //mVenueBottomSheet = new BottomSheetVenue();
    }

    private void setViewsParams(){
        mNavigationLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mSearchLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mVenueListLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        //mLocationView.setBackgroundColor(Color.argb(255, 235, 235, 235));
        //mLocationView.getLocationWindow().setStickToBorder(true);
        mItemDivider.setDividerColor(ContextCompat.getColor(requireActivity(), R.color.colorBackground));
        mItemDivider.setLastItemDecorated(false);
        mVenueListView.addItemDecoration(mItemDivider);

        //mLocationView.getLocationWindow().setPickRadius(10);
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
        //mMakeRouteSheet.setOnTouchListener((view, motionEvent) -> true);

        /*mStartRouteButton.setOnClickListener(v ->
        {
            if (mPinPoint == null && mToVenue == null) {
                Toast.makeText(requireActivity(), R.string.navigation_destination_select, Toast.LENGTH_SHORT).show();
                return;
            }

            if (mFromVenue == null && mFromPoint == null && mSelectMapPoint) return;

            onMakeRoute();
        });*/

        //mAdjustModeButton.setOnClickListener(v -> toggleAdjustMode());

        /*mLocationView.getLocationWindow().addPickListener(new PickListener() {
            @Override
            public void onMapObjectPickComplete(MapObjectPickResult mapObjectPickResult, PointF pointF) {
            }

            @Override
            public void onMapFeaturePickComplete(HashMap<String, String> hashMap, PointF pointF) {
                if (hashMap != null && hashMap.containsKey(KEY_VENUE_NAME)) {
                    for (int i = 0; i < mSublocation.getVenues().size(); i++) {
                        Venue v = mSublocation.getVenues().get(i);
                        if (v.getName().equals(hashMap.get(KEY_VENUE_NAME))) {
                            mPinVenue = v;
                            showVenueBottomSheet();
                            break;
                        }
                    }
                }
            }
        });

        mLocationView.getLocationWindow().addInputListener(new InputListener() {
            @Override
            public void onViewTap(PointF pointF) {
                handleClick(pointF.x, pointF.y);
            }

            @Override
            public void onViewDoubleTap(PointF pointF) {

            }

            @Override
            public void onViewLongTap(PointF pointF) {

            }

        });*/

        /*mLocationView.getLocationWindow().addCameraListener(new CameraListener() {
            @Override
            public void onCameraPositionChanged(CameraUpdateReason cameraUpdateReason, boolean b) {
                if (!b && mAdjustMode) {
                    toggleAdjustMode();
                }
            }
        });*/

    }

    @SuppressLint("UseRequireInsteadOfGet")
    private void showVenueBottomSheet(){
        //String titleText = mPinVenue.getName();
        /*if (titleText.length() > 25)
            titleText = titleText.substring(0, 24) + "…";*/

        /*String categoryText = mPinVenue.getName();
        if (categoryText.length() > 30)
            categoryText = categoryText.substring(0, 28) + "…";*/

        //String bm = new String(Base64.decode(mPinVenue.getImage(null), Base64.DEFAULT), StandardCharsets.UTF_8);
        //String bm = mPinVenue.getImageUrl();

        //mVenueBottomSheet.setSheetTitle(titleText);
        //mVenueBottomSheet.setDescription(mPinVenue.getDescript());
        //mVenueBottomSheet.setImageRef(bm);
        //mVenueBottomSheet.setRouteButtonVisibility(mFromPoint == null ? GONE : VISIBLE);
        /*mVenueBottomSheet.setRouteButtonClick(v -> {
            mPinPoint = null;
            mToVenue = mPinVenue;
            String title = mPinVenue.getName();
            if (title.length() > 20)
                title = title.substring(0, 18) + "";
            updateDestinationText("To:       " + title);
            if (mVenueBottomSheet.isAdded()) mVenueBottomSheet.dismiss();
            hideAndShowBottomSheets(null, mMakeRouteBehavior, BottomSheetBehavior.STATE_EXPANDED);
        });*/

        /////////mVenueBottomSheet.show(getParentFragmentManager(), null);
        //hideAndShowBottomSheets(mMakeRouteBehavior, null, BottomSheetBehavior.STATE_COLLAPSED);
    }

    /*public void onMakeRoute() {

        if (mSelectMapPoint) {
            mTargetPoint = mPinPoint;
            mTargetVenue = mToVenue;
            mPinPoint = null;
            mPinVenue = null;
            mToVenue = null;
            return;
        }

        if (mPinPoint != null) {
            mTargetPoint = mPinPoint;
            mTargetVenue = null;
            mPinPoint = null;
            mPinVenue = null;
            mToVenue = null;

            Log.d(TAG, "Set target point");

            NavigineSdkManager.RouteManager.setTarget(mTargetPoint);

        } else if (mToVenue != null) {
            mTargetVenue = mToVenue;
            mTargetPoint = null;
            mPinVenue = null;
            mToVenue = null;
            mPinPoint = null;

            Log.d(TAG, "Set venue target " + mTargetVenue.getId());

            NavigineSdkManager.RouteManager.setTarget(new LocationPoint(mTargetVenue.getPoint(), mLocation.getId(), mSublocation.getId()));
        }
        NavigineSdkManager.RouteManager.addRouteListener(mRouteListener);
        if (mVenueBottomSheet.isAdded()) mVenueBottomSheet.dismiss();
        hideAndShowBottomSheets(null, mMakeRouteBehavior, BottomSheetBehavior.STATE_HIDDEN);

        setRoutingFlag();
    }

    private void setRoutingFlag() {
        mRouting = true;
    }

    private void hideAndShowBottomSheets(@Nullable BottomSheetBehavior hideFirst, @Nullable BottomSheetBehavior show, int showState) {
        if (hideFirst != null) hideFirst.setState(BottomSheetBehavior.STATE_HIDDEN);
        if (show != null) show.setState(showState);
        if (show != null && show == mMakeRouteBehavior) {
            setActiveMakeRouteButton(false, false);
            if (mAdjustMode) {
                toggleAdjustMode();
            }
        }
    }

    private void setActiveMakeRouteButton(boolean isSelectMapPoint, boolean isGiveChoose) {
        mSelectMapPoint = isSelectMapPoint;
        if (isGiveChoose) {
            mFromPoint = null;
            mFromVenue = null;
        }
        if (mSelectMapPoint) {
            mFromCurrentText.setText("From: Select Point on Map");
            mFromCurrentText.setTextColor(getResources().getColor(R.color.colorError));
            mFromImageView.setImageResource(R.drawable.ic_to_point);
            updatePinIconState(mPinIconFrom, true);
        } else {
            mFromCurrentText.setText("From: Current Location");
            mFromCurrentText.setTextColor(getResources().getColor(R.color.colorPrimary));
            mFromImageView.setImageResource(R.drawable.ic_current_point);
            updatePinIconState(mPinIconFrom, false);
        }

    }

    private void updatePinIconState(IconMapObject pinIcon, boolean visible) {
        if (pinIcon != null) pinIcon.setVisible(visible);
    }

    public void toggleAdjustMode() {
        if (mPositionLocationPoint == null)
            //showWarningTemp(getString(R.string.err_navigation_position_define), 1500);
            Toast.makeText(getContext(),"Elver",Toast.LENGTH_SHORT).show();
        else {
            mAdjustMode = !mAdjustMode;
            mAdjustModeButton.setSelected(mAdjustMode);
        }
    }*/

    private void initLocationViewObjects() {


    }

    private void initAdapters(){
        mVenueListAdapter = new VenueListAdapter();
        //mVenuesIconsListAdapter = new VenuesIconsListAdapter();
    }

    private void setAdapters(){
        mVenueListView.setAdapter(mVenueListAdapter);
        //mVenueIconsListView.setAdapter(mVenuesIconsListAdapter);
    }

    private void setObservers(){
        /*viewModel.mLocation.observe(getViewLifecycleOwner(), location -> {


            if(mLocationLoaded){


                //updateFilteredVenuesIconsList();

                if (isVisible()) loadMap();
            }

        });*/
    }

    /*private void updateFilteredVenuesIconsList() {
        mFilteredVenueIconsList.clear();

        Map<Integer, String> mCategoriesIdsAll = new HashMap();
        List<Integer> mCategoriesIdsCurr = new ArrayList<>();

        for (Category category : mLocation.getCategories()) {
            mCategoriesIdsAll.put(category.getId(), category.getName());
        }
        for (Venue venue : mVenuesList) {
            mCategoriesIdsCurr.add(venue.getCategoryId());
        }
        mCategoriesIdsAll.keySet().retainAll(mCategoriesIdsCurr);

        for (VenueIconObj venueIconObj : VenueIconsListProvider.VenueIconsList) {
            if (mCategoriesIdsAll.containsValue(venueIconObj.getCategoryName())) {
                mFilteredVenueIconsList.add(new VenueIconObj(venueIconObj.getImageDrawable(), venueIconObj.getCategoryName()));
            }
        }
    }*/

    private void resetLocationFlags() {
        //mLocationChanged = false;
        mLocationLoaded  = false;
    }

    /*private void onMapLoaded() {
        Venue venue = findVenueById(mVenueId);
        if (venue != null) zoomToVenue(venue);
        mVenueId = -1;
    }*/

    /*private Venue findVenueById(int venueId) {
        if (mLocation != null) {
            for (Sublocation sublocation : mLocation.getSublocations()) {
                Venue venue = sublocation.getVenueById(venueId);
                if (venue != null) return venue;
            }
        }
        return null;
    }*/

    private void loadMap() {

        //int index = mLocation.getSublocations().indexOf(mSublocation);

        //loadSublocation();

        //hideLoadProgress();
        resetLocationFlags();
        //onMapLoaded();
    }

   /* private void loadSublocation() {
        mLocationView.post(() -> {
            mLocationView.getLocationWindow().setSublocationId(mSublocationId);
            float pixelWidth = mLocationView.getWidth() / getResources().getDisplayMetrics().density;
            mLocationView.getLocationWindow().setMaxZoomFactor((pixelWidth * 16.f) / mSublocation.getWidth());
            mLocationView.getLocationWindow().setMinZoomFactor((pixelWidth / 16.f) / mSublocation.getWidth());
            mLocationView.getLocationWindow().setZoomFactor(pixelWidth / mSublocation.getWidth());
            mLocationView.getLocationWindow().applyFilter("", getVenueLayerExp());
            setupZoomCameraDefault();
            //selectSublocationListItem(index);
        });
    }*/

    private String getVenueLayerExp() {
        return "base:pois:venues";
    }

    /*private void selectSublocationListItem(int index) {
        mSublocationsListView.setItemChecked(index, true);
    }*/

    private void setupZoomCameraDefault() {
        //mZoomCameraDefault = mLocationView.getLocationWindow().getCamera().getZoom();
    }

    private void handleClick(float x, float y) {
        //if (mTargetPoint != null || mTargetVenue != null || mPinPoint != null || mPinVenue != null/*|| mMakeRouteBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN*/) {
            //cancelPin();
            return;
        //}
        //mLocationView.getLocationWindow().pickMapFeatureAt(new PointF(x, y));
    }

    private void cancelPin() {

        //if (hasTarget())
            return;

        /*mPinPoint = null;
        mPinVenue = null;
        mToVenue = null;
        mFromVenue = null;
        mFromPoint = null;

        mPolylineMapObject.setVisible(false);
        updatePinIconsState(false, mPinIconFrom, mPinIconTarget);*/
    }

    /*private void adjustDevice(Point point) {
        Camera camera = new Camera(point, mZoomCameraDefault * 2, 0);
        mLocationView.getLocationWindow().flyTo(camera, 1000, null);
    }


    private boolean hasTarget() {
        return mTargetPoint != null || mTargetVenue != null;
    }

    private void updatePinIconsState(boolean visible, IconMapObject... pinIcons) {
        for (IconMapObject pinIcon : pinIcons) if (pinIcon != null) pinIcon.setVisible(visible);
    }*/

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
            //showVenueIconsLayout();
            //populateVenueIconsLayout();
        } else {
            hideSearchCLoseBtn();
            //hideVenueIconsLayout();
            showVenueListLayout();
        }
        filterVenueListByQuery(query);
    }

    private void filterVenueListByQuery(String query) {
        mVenueListAdapter.filter(query);
    }

    private void hideVenueLayouts() {
        mVenueListLayout.setVisibility(GONE);
        //mVenueIconsLayout.setVisibility(GONE);
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

    /*private void showVenueIconsLayout() {
        mVenueIconsLayout.setVisibility(VISIBLE);
    }

    private void hideVenueIconsLayout() {
        mVenueIconsLayout.setVisibility(GONE);
    }*/

    private void showVenueListLayout() {
        mVenueListLayout.setVisibility(VISIBLE);
    }

    private void hideVenueListLayout() {
        mVenueListLayout.setVisibility(GONE);
    }

    /*private void populateVenueIconsLayout() {
        if (mVenueIconsLayout.getVisibility() == VISIBLE)
            mVenuesIconsListAdapter.updateList(getFilteredVenuesIconsList());
    }*/

    /*private List<VenueIconObj> getFilteredVenuesIconsList() {
        return mFilteredVenueIconsList;
    }*/

    private void hideSearchCLoseBtn() {
        mSearchBtnClose.setVisibility(GONE);
    }

    private void showSearchCLoseBtn() {
        mSearchBtnClose.setVisibility(VISIBLE);
    }


    private class StateReceiver extends BroadcastReceiver {
        //private ArrayList<VenueIconObj> filteredVenues = null;
        @Override
        public void onReceive(Context context, Intent intent) {
            /*if (intent != null && VENUE_SELECTED.equals(intent.getAction())) {
                String venueName = intent.getStringExtra(KEY_VENUE_NAME);
                onHandleCancelSearch();
                for (int i = 0; i < mSublocation.getVenues().size(); i++) {
                    Venue v = mSublocation.getVenues().get(i);
                    if (v.getName().equals(venueName)) {
                        mPinVenue = v;
                        showVenueBottomSheet();
                        break;
                    }
                }
            }*/
        }
    }

    /*private class PositionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case ACTION_POSITION_UPDATED:

                    float x = intent.getFloatExtra(NavigationService.KEY_POINT_X, -1f);
                    float y = intent.getFloatExtra(NavigationService.KEY_POINT_Y, -1f);
                    int locationId = intent.getIntExtra(NavigationService.KEY_LOCATION_ID, -1);
                    int sublocationId = intent.getIntExtra(NavigationService.KEY_SUBLOCATION_ID, -1);
                    double pointLocationHeading = intent.getDoubleExtra(NavigationService.KEY_LOCATION_HEADING, -1.0);

                    LocationPoint lp = new LocationPoint(new Point(x, y), locationId, sublocationId);

                    if (x == -1f || y == -1f || locationId == -1 || sublocationId == -1) return;

                    mPositionLocationPoint = lp;

                    if (pointLocationHeading != -1) {
                        if (!mOrientationPointState) {
                            mOrientationPointState = true;
                            mPositionIcon.setSize(48, 52);
                            mPositionIcon.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_current_point_direction_png));
                            //mPositionIcon.setAngle(pointLocationHeading);
                        }
                        //mPositionIcon.setAngleAnimated(pointLocationHeading, 1.0f, AnimationType.CUBIC);
                    } else {
                        if (mOrientationPointState) {
                            mOrientationPointState = false;
                            mPositionIcon.setSize(30, 30);
                            mPositionIcon.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_current_point_png));
                        }
                    }

                    if (mAdjustMode) {
                        int id = lp.getSublocationId();
                        if (mSublocation.getId() != id) {
                            mSublocation = mLocation.getSublocationById(id);
                            loadSublocation(/*mLocation.getSublocations().indexOf(mSublocation)*//*);
                        }
                        adjustDevice(lp.getPoint());
                    }
                    mFromPoint = lp;
                    mPositionIcon.setVisible(true);
                    if (mSetupPosition) {
                        mSetupPosition = false;
                        mPositionIcon.setPosition(mFromPoint);
                    } else {
                        mPositionIcon.setPositionAnimated(mFromPoint, 1.0f, AnimationType.CUBIC);
                    }
                    break;
                case ACTION_POSITION_ERROR:
                    mPositionLocationPoint = null;
                    mAdjustModeButton.setSelected(false);
                    mPositionIcon.setVisible(false);
                    String errMsg = intent.getStringExtra(ACTION_POSITION_ERROR);
                    if (errMsg != null) {
                        Log.e(TAG, getString(R.string.err_navigation_position_update) + ":" + errMsg);
                    }
                    break;
            }
        }
    }*/


}