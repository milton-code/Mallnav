package com.proyecto.mallnav.adapters.venues;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.mallnav.R;

public class VenueIconViewHolder extends RecyclerView.ViewHolder {

    protected ImageView icon  = null;
    protected TextView  title = null;

    public VenueIconViewHolder(@NonNull View itemView) {
        super(itemView);

        icon  = itemView.findViewById(R.id.li_venue_icon);
        title = itemView.findViewById(R.id.li_venue_title);
    }
}
