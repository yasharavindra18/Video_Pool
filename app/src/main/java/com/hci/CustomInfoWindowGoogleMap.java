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
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.custom_layout, null);

        TextView name_tv = view.findViewById(R.id.name);
        //TextView details_tv = view.findViewById(R.id.details);
        ImageView img = view.findViewById(R.id.pic);
        //Button record = (Button) view.findViewById(R.id.record);

        //TextView hotel_tv = view.findViewById(R.id.hotels);
        //TextView food_tv = view.findViewById(R.id.food);
        //TextView transport_tv = view.findViewById(R.id.transport);

        name_tv.setText(marker.getTitle());


        Log.i("id", marker.getSnippet());
        //details_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

//      int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),
//                "drawable", context.getPackageName());

        img.setImageResource(R.drawable.event);

        //hotel_tv.setText("test");
        //food_tv.setText("test");
        //transport_tv.setText("test");

        return view;
    }


}