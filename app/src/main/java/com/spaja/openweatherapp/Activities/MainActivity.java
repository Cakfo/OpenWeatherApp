package com.spaja.openweatherapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.spaja.openweatherapp.APIService.OpenWeatherAPI;
import com.spaja.openweatherapp.Model.GoogleAPIResponse;
import com.spaja.openweatherapp.Model.Main;
import com.spaja.openweatherapp.R;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Typeface tf;
    static final String API_KEY = "AIzaSyCNjTFU1Yh_SPK41QmR7CfKVv538eEG7fo";
    static final String TYPES = "(cities)";
    public ArrayList<String> resultList;
    EditText autoCompleteTextView;
    private Button search, delete, location;
    RecyclerView citiesRecycler;
    double lat, lon;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

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
                if (autoCompleteTextView.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Place can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, WeatherData.class);
                    i.putExtra("cityname", autoCompleteTextView.getText().toString());
                    startActivity(i);
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
                Intent i = new Intent(MainActivity.this, WeatherData.class);
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                startActivity(i);
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
                    citiesRecycler.setVisibility(View.GONE);}
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
    }
}
