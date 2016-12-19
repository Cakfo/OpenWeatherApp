package com.spaja.openweatherapp.model;

import java.util.ArrayList;

/**
 * Created by Spaja on 22-Nov-16.
 */

public class GoogleAPIResponse {

    ArrayList<Predictions> predictions;
    String status;

    public ArrayList<Predictions> getPredicitons() {
        return predictions;
    }

    public String getStatus() {
        return status;
    }
}
