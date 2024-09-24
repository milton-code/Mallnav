package com.proyecto.mallnav.adapters.venues;

import com.proyecto.mallnav.models.Venue;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_NAME;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_POINT;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_SUBLOCATION;
import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.proyecto.mallnav.R;

public class VenueViewHolder extends RecyclerView.ViewHolder {

    protected TextView venueName = null;

    public VenueViewHolder(@NonNull View itemView) {
        super(itemView);
        venueName = itemView.findViewById(R.id.li_venue_name);
    }

    public void bind(Venue venue) {
        venueName.setText(venue.getNombre());
        itemView.setOnClickListener(v -> {
            Intent i = new Intent(VENUE_SELECTED);
            i.putExtra(KEY_VENUE_NAME, venue.getNombre());
            //i.putExtra(KEY_VENUE_POINT, new float[]{venue.getPoint().getX(), venue.getPoint().getY()});
            v.getContext().sendBroadcast(i);
        });
    }
}
