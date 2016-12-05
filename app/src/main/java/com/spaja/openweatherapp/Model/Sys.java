package com.spaja.openweatherapp.Model;

import java.io.Serializable;

/**
 * Created by Spaja on 06-Nov-16.
 */

public class Sys{

    int type;
    int id;
    double message;
    String country;
    int sunrise;
    int sunset;

    public String getCountry() {
        return country;
    }

    public int getId() {
        return id;
    }

    public double getMessage() {
        return message;
    }

    public int getSunrise() {
        return sunrise;
    }

    public int getSunset() {
        return sunset;
    }

    public int getType() {
        return type;
    }
}
