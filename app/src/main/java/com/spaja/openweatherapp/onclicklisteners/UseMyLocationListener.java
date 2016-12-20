package com.spaja.openweatherapp.onclicklisteners;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.view.View;
import android.widget.Toast;

import com.spaja.openweatherapp.activities.WeatherData;

/**
 * Created by Spaja on 20-Dec-16.
 */

public class UseMyLocationListener implements View.OnClickListener {

    private double lat;
    private double lon;
    private LocationManager locationManager;
    private Context context;

    public UseMyLocationListener(Context context, LocationManager locationManager, double lat, double lon) {
        this.locationManager = locationManager;
        this.lat = lat;
        this.lon = lon;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent i = new Intent(context, WeatherData.class);
            i.putExtra("lat", lat);
            i.putExtra("lon", lon);
            context.startActivity(i);
        } else {
            Toast.makeText(context, "Please enable your GPS", Toast.LENGTH_SHORT).show();
        }
    }
}
