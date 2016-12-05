package com.spaja.openweatherapp.APIService;

import com.spaja.openweatherapp.Model.ForecastResponse;
import com.spaja.openweatherapp.Model.GoogleAPIResponse;
import com.spaja.openweatherapp.Model.ResponseData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by Spaja on 03-Nov-16.
 */

public interface OpenWeatherAPI {

    @GET("weather")
    Call<ResponseData> getWeatherData(@Query("q") String q, @Query("APPID") String appid, @Query("units") String units);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("weather")
    Call<ResponseData> getWeatherDataLocation(@Query("lat") double lat, @Query("lon") double lon, @Query("APPID") String appid, @Query("units") String units);

    Retrofit retrofit_geo = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("forecast")
    Call<ForecastResponse> getForecast(@Query("q") String q, @Query("APPID") String appid, @Query("units") String units);

    Retrofit retrofit_forecast = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("forecast")
    Call<ForecastResponse> getForecastLocation(@Query("lat") double q, @Query("lon") double lon, @Query("APPID") String appid, @Query("units") String units);

    Retrofit retrofit_forecast_geo = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("json")
    Call<GoogleAPIResponse> getAutoComplete(@Query("input") String input, @Query("key") String key, @Query("types") String types);

    Retrofit retrofit_googleAPI = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/autocomplete/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    OpenWeatherAPI service = retrofit.create(OpenWeatherAPI.class);
    OpenWeatherAPI forecast_service = retrofit_forecast.create(OpenWeatherAPI.class);
    OpenWeatherAPI places_service = retrofit_googleAPI.create(OpenWeatherAPI.class);
    OpenWeatherAPI geo_service = retrofit_geo.create(OpenWeatherAPI.class);
    OpenWeatherAPI forecast_geo_service = retrofit_forecast_geo.create(OpenWeatherAPI.class);
}
