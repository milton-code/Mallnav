package com.proyecto.mallnav.ui.dialogs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.proyecto.mallnav.utils.Constants.CATEGORY_SELECTED;
import static com.proyecto.mallnav.utils.Constants.KEY_CATEGORY_ID;
import static com.proyecto.mallnav.utils.Constants.KEY_CATEGORY_NAME;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_NAME;
import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.proyecto.mallnav.R;
import com.proyecto.mallnav.adapters.categorias.CategoryListAdapter;
import com.proyecto.mallnav.adapters.venues.VenueListAdapter;
import com.proyecto.mallnav.models.Categoria;
import com.proyecto.mallnav.models.Venue;

import com.proyecto.mallnav.utils.ContentManager;
import com.proyecto.mallnav.utils.VenueProvider;

import java.util.List;
import java.util.Objects;


public class UpdateFragment extends DialogFragment {
    private UpdateFragment.StateReceiver mStateReceiver = null;
    private IntentFilter mStateReceiverFilter = null;
    private Categoria categorySelected = null;
    private List<Categoria> categorias = null;
    private FrameLayout mCategoryListLayout = null;
    private MaterialButton updateVenueButton = null;
    private EditText updateVenueName = null;
    private CategoryListAdapter mCategoryListAdapter = null;
    private TextView categoryTextView = null;
    private RecyclerView mCategoryListView = null;
    private MaterialDividerItemDecoration mItemDivider = null;
    private LinearLayout selectorCategory = null;
    private Venue mPinVenue = null;

    public UpdateFragment(Venue mPinVenue){
        this.mPinVenue = mPinVenue;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadcastReceiver();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        initViews(view);
        setViewsParams();
        initAdapters();
        setAdapters();
        setViewsListeners();
        return view;
    }

    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }

    @Override
    public void onResume() {
        super.onResume();
        //onHiddenChanged(!isVisible());
        addListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListeners();
    }

    private void initViews(View view) {
        updateVenueName = view.findViewById(R.id.fragment__update_venue_name);
        categoryTextView = view.findViewById(R.id.category_selected);
        mCategoryListLayout = view.findViewById(R.id.fragment__update_category_listview);
        mCategoryListView = view.findViewById(R.id.recycler_list_category);
        updateVenueButton = view.findViewById(R.id.update_venue_button);
        selectorCategory = view.findViewById(R.id.selected_category_container);
        mItemDivider = new MaterialDividerItemDecoration(requireActivity(), MaterialDividerItemDecoration.VERTICAL);

    }
    private void setViewsParams() {
        selectorCategory.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mCategoryListLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        mItemDivider.setDividerColor(ContextCompat.getColor(requireActivity(), R.color.colorBackground));
        mItemDivider.setLastItemDecorated(false);
        mCategoryListView.addItemDecoration(mItemDivider);
        if(mPinVenue.getCategoria_id() != -1){
            updateVenueButton.setText("Actualizar venue");
        }
        else {
            updateVenueButton.setText("Agregar venue");
        }
    }

    private void initBroadcastReceiver() {
        mStateReceiver = new UpdateFragment.StateReceiver();
        mStateReceiverFilter = new IntentFilter();
        mStateReceiverFilter.addAction(CATEGORY_SELECTED);
    }

    private void initAdapters(){
        categorias = VenueProvider.categoriaList;
        Log.d("Categoria","la lista de categorias tiene un size de: " + categorias.size());
        mCategoryListAdapter = new CategoryListAdapter(getContext(), categorias);
        //mCategoryListView.setActivated(true);
        //mCategoryListAdapter.notifyDataSetChanged();
    }

    private void setAdapters(){
        mCategoryListView.setAdapter(mCategoryListAdapter);
    }

    private void addListeners() {
        requireActivity().registerReceiver(mStateReceiver, mStateReceiverFilter);
    }

    private void removeListeners() {
        requireActivity().unregisterReceiver(mStateReceiver);
    }
    private void setViewsListeners() {
        selectorCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryListLayout();
            }
        });

        updateVenueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categorySelected != null){
                    String venueUpdtName = updateVenueName.getText().toString().trim();
                    ContentManager manager = new ContentManager(getContext());
                    manager.actualizarVenue(mPinVenue, venueUpdtName, categorySelected.getId());
                }
            }
        });
    }

    private void showCategoryListLayout() {
        mCategoryListLayout.setVisibility(VISIBLE);
    }

    private void hideCategoryListLayout() {
        mCategoryListLayout.setVisibility(GONE);
    }

    private class StateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
             if (intent != null && CATEGORY_SELECTED.equals(intent.getAction())) {
                String nombreCategoria = intent.getStringExtra(KEY_CATEGORY_NAME);
                int categoria_id = intent.getIntExtra(KEY_CATEGORY_ID,-1);
                categorySelected = new Categoria(categoria_id, nombreCategoria);
                categoryTextView.setText(nombreCategoria);
                hideCategoryListLayout();
            }
        }
    }

}