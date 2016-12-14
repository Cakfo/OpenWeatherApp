package com.spaja.openweatherapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.spaja.openweatherapp.APIService.OpenWeatherAPI;
import com.spaja.openweatherapp.Model.GoogleAPIResponse;
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

    private Typeface tf;
    static final String API_KEY = "AIzaSyCNjTFU1Yh_SPK41QmR7CfKVv538eEG7fo";
    static final String TYPES = "(cities)";
    public ArrayList<String> resultList;
    EditText autoCompleteTextView;
    private Button search, delete, location, clear_prefs;
    RecyclerView citiesRecycler;
    double lat, lon;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationManager locationManager;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    AlertDialog alertDialog;
    String cityName;
    private DrawerLayout DrawerLayout;
    private ListView DrawerList;
    ArrayAdapter myAdapter;
    ArrayList<String> newCitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

        try {
            FileInputStream fis = openFileInput("USER_DATA");
            ObjectInputStream ois = new ObjectInputStream(fis);
            newCitiesList = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (newCitiesList.size() == 0) {
            newCitiesList.add("Your List is Empty");
        }

        myAdapter = new ArrayAdapter<>(this,
                R.layout.list_view_item, newCitiesList);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.list_view_header, DrawerList, false);
        DrawerList.addHeaderView(header, null, false);
        DrawerList.setAdapter(myAdapter);
        DrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, WeatherData.class);
                i.putExtra("cityname", newCitiesList.get((int) id));
                startActivity(i);
                DrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
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
                                    if (newCitiesList.equals("Your List is Empty"))
                                        newCitiesList.add(autoCompleteTextView.getText().toString().split(",")[0]);
                                    try {
                                        FileOutputStream fos = openFileOutput("USER_DATA", Context.MODE_PRIVATE);
                                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                                        oos.writeObject(newCitiesList);
                                        oos.close();
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

    class AutoCompleteAdapter extends RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder> {
        private ArrayList<String> mDataset;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView cityName;

            ViewHolder(View v) {
                super(v);
                cityName = (TextView) v.findViewById(R.id.city_name);
            }
        }

        AutoCompleteAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public AutoCompleteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.autocomplete_list_item, parent, false);
            AutoCompleteAdapter.ViewHolder vh = new AutoCompleteAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final AutoCompleteAdapter.ViewHolder holder, final int position) {

            holder.cityName.setText(mDataset.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCompleteTextView.setText(mDataset.get(position));
                    //citiesRecycler.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private void initializeVariables() {
        tf = Typeface.createFromAsset(getAssets(), "robotoslablight.ttf");
        search = (Button) findViewById(R.id.b_search);
        delete = (Button) findViewById(R.id.b_delete);
        autoCompleteTextView = (EditText) findViewById(R.id.et_city_name);
        autoCompleteTextView.setTypeface(tf);
        location = (Button) findViewById(R.id.b_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        preferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
        DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        DrawerList = (ListView) findViewById(R.id.left_drawer);
        clear_prefs = (Button) findViewById(R.id.clear_prefs);
        cityName = "Default Value";
        newCitiesList = new ArrayList<>();
    }
}
