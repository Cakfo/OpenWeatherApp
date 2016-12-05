package com.spaja.openweatherapp.Model;

import java.io.Serializable;

/**
 * Created by Spaja on 06-Nov-16.
 */

public class Coord{

    double lon;
    double lat;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
