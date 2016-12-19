package com.spaja.openweatherapp.model;

/**
 * Created by Spaja on 06-Nov-16.
 */

public class Main{

    double temp;
    double pressure;
    int humidity;
    double temp_min;
    double temp_max;

    public int getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public double getTemp() {
        return temp;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getTemp_min() {
        return temp_min;
    }
}
