package com.hci;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hci.activities.R;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_layout, null);
        TextView name_tv = view.findViewById(R.id.name);
        ImageView img = view.findViewById(R.id.pic);
        name_tv.setText(marker.getTitle());
        Log.i("id", marker.getSnippet());
        img.setImageResource(R.drawable.event);
        return view;
    }


}