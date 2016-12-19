package com.spaja.openweatherapp.model;

import java.util.ArrayList;

/**
 * Created by Spaja on 17-Nov-16.
 */

public class DataList {

    int dt;
    Main main;
    ArrayList<Weather> weather;
    String dt_txt;

    public String getDt_txt() {
        return dt_txt;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public int getDt() {
        return dt;
    }

    public Main getMain() {
        return main;
    }

}
