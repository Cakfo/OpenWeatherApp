package com.spaja.openweatherapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.spaja.openweatherapp.adapters.AutoCompleteAdapter;
import com.spaja.openweatherapp.apiservice.OpenWeatherAPI;
import com.spaja.openweatherapp.adapters.DrawerRecyclerViewAdapter;
import com.spaja.openweatherapp.model.GoogleAPIResponse;
import com.spaja.openweatherapp.R;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Serializable {

    static final String API_KEY = "AIzaSyCNjTFU1Yh_SPK41QmR7CfKVv538eEG7fo";
    static final String TYPES = "(cities)";
    public static final String FILE_NAME = "USER_DATA";
    public ArrayList<String> resultList;
    EditText autoCompleteTextView;
    private Button search, delete, location, clear_prefs;
    RecyclerView citiesRecycler;
    double lat, lon;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationManager locationManager;
    AlertDialog alertDialog;
    String cityName;
    public DrawerLayout DrawerLayout;
    ArrayList<String> newCitiesList;
    private RecyclerView drawerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

        try {
            readFromFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (newCitiesList.size() == 0) {
            newCitiesList.add("Your List is Empty");
        }

        RecyclerView.Adapter drawerRecyclerAdapter = new DrawerRecyclerViewAdapter(newCitiesList, DrawerLayout);
        drawerRecyclerView.setAdapter(drawerRecyclerAdapter);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPredictions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoCompleteTextView.getText().toString().trim().length() != 0) {
                    alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Save city name")
                            .setMessage("Do you want to save this city?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    newCitiesList.add(autoCompleteTextView.getText().toString().split(",")[0]);
                                    try {
                                        writeToFile(newCitiesList);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Intent i = new Intent(MainActivity.this, WeatherData.class);
                                    i.putExtra("cityname", autoCompleteTextView.getText().toString());
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent i = new Intent(MainActivity.this, WeatherData.class);
                                    i.putExtra("cityname", autoCompleteTextView.getText().toString());
                                    startActivity(i);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Toast.makeText(MainActivity.this, "Place can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent i = new Intent(MainActivity.this, WeatherData.class);
                    i.putExtra("lat", lat);
                    i.putExtra("lon", lon);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, "Please enable your GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clear_prefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private ArrayList<String> readFromFile() throws IOException, ClassNotFoundException {
        FileInputStream fis = openFileInput(FILE_NAME);
        ObjectInputStream ois = new ObjectInputStream(fis);
        newCitiesList = (ArrayList<String>) ois.readObject();
        ois.close();
        return newCitiesList;

    }

    private void writeToFile(ArrayList<String> cityList) throws IOException {
        FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(cityList);
        oos.close();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Can't connect too Google services", Toast.LENGTH_SHORT).show();
    }

    public void getPredictions(String input) {
        OpenWeatherAPI.places_service.getAutoComplete(input, API_KEY, TYPES).enqueue(new Callback<GoogleAPIResponse>() {
            @Override
            public void onResponse(Call<GoogleAPIResponse> call, Response<GoogleAPIResponse> response) {
                resultList = new ArrayList<>();
                for (int i = 0; i < response.body().getPredicitons().size(); i++) {
                    resultList.add(response.body().getPredicitons().get(i).getDescription());
                }
                AutoCompleteAdapter adapter = new AutoCompleteAdapter(resultList);
                citiesRecycler = (RecyclerView) findViewById(R.id.cities_rv);
                citiesRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                citiesRecycler.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<GoogleAPIResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initializeVariables() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "robotoslablight.ttf");
        search = (Button) findViewById(R.id.b_search);
        delete = (Button) findViewById(R.id.b_delete);
        autoCompleteTextView = (EditText) findViewById(R.id.et_city_name);
        autoCompleteTextView.setTypeface(tf);
        location = (Button) findViewById(R.id.b_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        clear_prefs = (Button) findViewById(R.id.clear_prefs);
        cityName = "Default Value";
        newCitiesList = new ArrayList<>();
        drawerRecyclerView = (RecyclerView) findViewById(R.id.left_drawer);
        RecyclerView.LayoutManager drawerRecyclerLayoutManager = new LinearLayoutManager(this);
        drawerRecyclerView.setLayoutManager(drawerRecyclerLayoutManager);
        DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }
}
