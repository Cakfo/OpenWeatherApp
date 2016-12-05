package com.spaja.openweatherapp.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Spaja on 03-Nov-16.
 */

public class ResponseData{

    ArrayList<Weather> weather;
    Coord coord;
    Main main;
    int visibility;
    Wind wind;
    int id;
    String name;

    public int getCod() {
        return cod;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    int cod;

    public Sys getSys() {
        return sys;
    }

    Sys sys;


    public Clouds getClouds() {
        return clouds;
    }

    Clouds clouds;

    public int getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Main getMain() {
        return main;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        this.weather = weather;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }
}
