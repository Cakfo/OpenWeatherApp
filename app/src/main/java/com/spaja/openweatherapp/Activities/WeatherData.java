package com.spaja.openweatherapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.spaja.openweatherapp.APIService.OpenWeatherAPI;
import com.spaja.openweatherapp.Model.DataList;
import com.spaja.openweatherapp.Model.ForecastResponse;
import com.spaja.openweatherapp.Model.ResponseData;
import com.spaja.openweatherapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherData extends AppCompatActivity {


    private final static String API_KEY = "77ce1660c02fe7dec7ec89337471a613";
    private final static String units = "metric";
    private String cityname, passedData;
    private ImageView pin_icon, iv_condition;
    private TextView name, date, temp, temp_min, temp_max, condition, tv_temp;
    ResponseData responsedata;
    private Typeface tf;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int expandedPosition = -1;
    double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_data);

        initializeVariables();
        passedData = getIntent().getStringExtra("cityname");
        if (passedData != null){
            cityname = passedData.split(", ")[0];
            getWeather();
            getForecast();
        } else {
            lat = getIntent().getDoubleExtra("lat", 0);
            lon = getIntent().getDoubleExtra("lon", 0);
            getWeatherGeo();
            getForecastGeo();
        }

        Glide.with(this).load(R.drawable.newpin).into(pin_icon);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        date.setText(formattedDate);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void getWeatherGeo() {
        OpenWeatherAPI.geo_service.getWeatherDataLocation(lat, lon, API_KEY, units).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                int id;
                responsedata = response.body();
                name.setText(responsedata.getName());
                temp.setText((int) responsedata.getMain().getTemp() + "°C");
                temp_min.setText(responsedata.getMain().getTemp_min() + "°C");
                temp_max.setText(responsedata.getMain().getTemp_max() + "°C");
                condition.setText(responsedata.getWeather().get(0).getDescription());
                id = responsedata.getWeather().get(0).getId();
                if (id >= 200 & id < 300) {
                    Glide.with(WeatherData.this).load(R.drawable.thunderstorms).into(iv_condition);
                } else if (id >= 300 & id < 500) {
                    Glide.with(WeatherData.this).load(R.drawable.slight_drizzle).into(iv_condition);
                } else if (id >= 500 & id < 600) {
                    Glide.with(WeatherData.this).load(R.drawable.drizzle).into(iv_condition);
                } else if (id >= 600 & id < 700) {
                    Glide.with(WeatherData.this).load(R.drawable.snow).into(iv_condition);
                } else if (id >= 700 & id < 800) {
                    Glide.with(WeatherData.this).load(R.drawable.haze).into(iv_condition);
                } else if (id == 800) {
                    Glide.with(WeatherData.this).load(R.drawable.sunny).into(iv_condition);
                } else if (id > 800 & id < 900) {
                    Glide.with(WeatherData.this).load(R.drawable.cloudy).into(iv_condition);
                } else if (id >= 900 & id < 910) {
                    Glide.with(WeatherData.this).load(R.drawable.cloudy).into(iv_condition);
                } else {
                    Glide.with(WeatherData.this).load(R.drawable.cloudy).into(iv_condition);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {

            }
        });
    }

    private void getForecastGeo(){
        OpenWeatherAPI.forecast_geo_service.getForecastLocation(lat, lon, API_KEY, units).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                ArrayList<DataList> list = new ArrayList<>();
                for (int i = 0; i < response.body().getList().size(); i++) {
                    String sub = response.body().getList().get(i).getDt_txt().substring(11, 13);
                    if (sub.equals("12")) {
                        list.add(response.body().getList().get(i));
                    }
                }
                mAdapter = new MyAdapter(list);
                mRecyclerView.setAdapter(mAdapter);
                tv_temp.setText((int) response.body().getList().get(0).getMain().getTemp() + "°C");
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {

            }
        });

    }

    private void getForecast() {
        OpenWeatherAPI.forecast_service.getForecast(cityname, API_KEY, units).enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                ArrayList<DataList> list = new ArrayList<>();
                for (int i = 0; i < response.body().getList().size(); i++) {
                    String sub = response.body().getList().get(i).getDt_txt().substring(11, 13);
                    if (sub.equals("12")) {
                        list.add(response.body().getList().get(i));
                    }
                }
                mAdapter = new MyAdapter(list);
                mRecyclerView.setAdapter(mAdapter);
                tv_temp.setText((int) response.body().getList().get(0).getMain().getTemp() + "°C");

            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                tv_temp.setText("error");
            }
        });
    }

    private void getWeather() {
        OpenWeatherAPI.service.getWeatherData(cityname, API_KEY, units).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                int id;
                responsedata = response.body();
                name.setText(responsedata.getName());
                temp.setText((int) responsedata.getMain().getTemp() + "°C");
                temp_min.setText(responsedata.getMain().getTemp_min() + "°C");
                temp_max.setText(responsedata.getMain().getTemp_max() + "°C");
                condition.setText(responsedata.getWeather().get(0).getDescription());
                id = responsedata.getWeather().get(0).getId();
                if (id >= 200 & id < 300) {
                    Glide.with(WeatherData.this).load(R.drawable.thunderstorms).into(iv_condition);
                } else if (id >= 300 & id < 500) {
                    Glide.with(WeatherData.this).load(R.drawable.slight_drizzle).into(iv_condition);
                } else if (id >= 500 & id < 600) {
                    Glide.with(WeatherData.this).load(R.drawable.drizzle).into(iv_condition);
                } else if (id >= 600 & id < 700) {
                    Glide.with(WeatherData.this).load(R.drawable.snow).into(iv_condition);
                } else if (id >= 700 & id < 800) {
                    Glide.with(WeatherData.this).load(R.drawable.haze).into(iv_condition);
                } else if (id == 800) {
                    Glide.with(WeatherData.this).load(R.drawable.sunny).into(iv_condition);
                } else if (id > 800 & id < 900) {
                    Glide.with(WeatherData.this).load(R.drawable.cloudy).into(iv_condition);
                } else if (id >= 900 & id < 910) {
                    Glide.with(WeatherData.this).load(R.drawable.cloudy).into(iv_condition);
                } else {
                    Glide.with(WeatherData.this).load(R.drawable.cloudy).into(iv_condition);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                name.setText("error");
            }
        });
    }

    private void initializeVariables() {
        pin_icon = (ImageView) findViewById(R.id.iv_pin_icon);
        tf = Typeface.createFromAsset(getAssets(), "robotoslablight.ttf");
        name = (TextView) findViewById(R.id.tv_cityname);
        name.setTypeface(tf);
        date = (TextView) findViewById(R.id.tv_date);
        date.setTypeface(tf);
        temp = (TextView) findViewById(R.id.tv_temp);
        temp_min = (TextView) findViewById(R.id.tv_temp_min);
        temp_min.setTypeface(tf);
        temp_max = (TextView) findViewById(R.id.tv_temp_max);
        temp_max.setTypeface(tf);
        condition = (TextView) findViewById(R.id.tv_condition);
        iv_condition = (ImageView) findViewById(R.id.iv_condition);
        tv_temp = (TextView) findViewById(R.id.tv_temp);

    }



    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<DataList> mDataset;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextViewDay;
            ImageView mImageView;
            TextView mTextViewTemp;
            LinearLayout llExpandArea, divider, bottom_divider;

            ViewHolder(View v) {
                super(v);
                mTextViewDay = (TextView) v.findViewById(R.id.tv_day);
                mImageView = (ImageView) v.findViewById(R.id.iv_day);
                mTextViewTemp = (TextView) v.findViewById(R.id.tv_tempday);
                llExpandArea = (LinearLayout) v.findViewById(R.id.llExpandArea);
                divider = (LinearLayout) v.findViewById(R.id.divider);
                bottom_divider = (LinearLayout) v.findViewById(R.id.bottom_divider);
            }
        }

        MyAdapter(ArrayList<DataList> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (position == expandedPosition && holder.llExpandArea.getVisibility() == View.GONE) {
                holder.llExpandArea.setVisibility(View.VISIBLE);
                holder.divider.setVisibility(View.GONE);
                holder.bottom_divider.setVisibility(View.VISIBLE);

            } else {
                holder.llExpandArea.setVisibility(View.GONE);
                holder.divider.setVisibility(View.VISIBLE);
                holder.bottom_divider.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(position != expandedPosition) {
                        expandedPosition = position;
                    }
                    notifyDataSetChanged();
                }
            });
            holder.mTextViewTemp.setText((int) mDataset.get(position).getMain().getTemp() + "°C");
            int id = mDataset.get(position).getWeather().get(0).getId();
            Date date = new Date(mDataset.get(position).getDt() * 1000L);
            SimpleDateFormat df = new SimpleDateFormat("EEE-MMM-yyyy");
            String formattedDate = df.format(date);
            String final_date = formattedDate.substring(0, 3);

            switch (final_date) {
                case "Mon":
                    holder.mTextViewDay.setText("MONDAY");
                    break;
                case "Tue":
                    holder.mTextViewDay.setText("TUESDAY");
                    break;
                case "Wed":
                    holder.mTextViewDay.setText("WEDNESDAY");
                    break;
                case "Thu":
                    holder.mTextViewDay.setText("THRUSDAY");
                    break;
                case "Fri":
                    holder.mTextViewDay.setText("FRIDAY");
                    break;
                case "Sat":
                    holder.mTextViewDay.setText("SATURDAY");
                    break;
                case "Sun":
                    holder.mTextViewDay.setText("SUNDAY");
                    break;
            }
            if (id >= 200 & id < 300) {
                Glide.with(WeatherData.this).load(R.drawable.thunderstorms).into(holder.mImageView);
            } else if (id >= 300 & id < 500) {
                Glide.with(WeatherData.this).load(R.drawable.slight_drizzle).into(holder.mImageView);
            } else if (id >= 500 & id < 600) {
                Glide.with(WeatherData.this).load(R.drawable.drizzle).into(holder.mImageView);
            } else if (id >= 600 & id < 700) {
                Glide.with(WeatherData.this).load(R.drawable.snow).into(holder.mImageView);
            } else if (id >= 700 & id < 800) {
                Glide.with(WeatherData.this).load(R.drawable.haze).into(holder.mImageView);
            } else if (id == 800) {
                Glide.with(WeatherData.this).load(R.drawable.sunny).into(holder.mImageView);
            } else if (id > 800 & id < 900) {
                Glide.with(WeatherData.this).load(R.drawable.cloudy).into(holder.mImageView);
            } else if (id >= 900 & id < 910) {
                Glide.with(WeatherData.this).load(R.drawable.cloudy).into(holder.mImageView);
            } else {
                Glide.with(WeatherData.this).load(R.drawable.cloudy).into(holder.mImageView);
            }
        }


        @Override
        public int getItemCount() {
            return mDataset.size();

        }
    }
}

