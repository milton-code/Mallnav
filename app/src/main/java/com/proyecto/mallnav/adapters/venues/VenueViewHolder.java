package com.proyecto.mallnav.adapters.venues;

import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_POINT;
import static com.proyecto.mallnav.utils.Constants.KEY_VENUE_SUBLOCATION;
import static com.proyecto.mallnav.utils.Constants.VENUE_SELECTED;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.navigine.idl.java.Location;
import com.navigine.idl.java.Venue;
import com.proyecto.mallnav.R;

public class VenueViewHolder extends RecyclerView.ViewHolder {

    protected TextView venueName        = null;
    protected TextView venueSublocation = null;

    public VenueViewHolder(@NonNull View itemView) {
        super(itemView);
        venueName        = itemView.findViewById(R.id.li_venue_name);
        venueSublocation = itemView.findViewById(R.id.li_venue_sublocation);
    }

    public void bind(Venue venue, Location location) {
        venueName.       setText(venue.getName());
        venueSublocation.setText(location.getSublocationById(venue.getSublocationId()).getName());
        itemView.        setOnClickListener(v -> {
            Intent i = new Intent(VENUE_SELECTED);
            i.putExtra(KEY_VENUE_SUBLOCATION, venue.getSublocationId());
            i.putExtra(KEY_VENUE_POINT, new float[]{venue.getPoint().getX(), venue.getPoint().getY()});
            v.getContext().sendBroadcast(i);
        });
    }
}
