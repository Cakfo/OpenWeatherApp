package com.spaja.openweatherapp.adapters;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spaja.openweatherapp.activities.WeatherData;
import com.spaja.openweatherapp.R;

import java.util.ArrayList;


public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mDataset;
    private DrawerLayout mDrawerLayout;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        ViewHolder(android.view.View itemView) {

            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_view_tv);
        }
    }

    public DrawerRecyclerViewAdapter(ArrayList<String> dataSet, DrawerLayout drawerLayout) {
        mDataset = dataSet;
        mDrawerLayout = drawerLayout;
    }

    @Override
    public DrawerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DrawerRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.textView.setText(mDataset.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent i = new Intent(holder.itemView.getContext(), WeatherData.class);
                i.putExtra("cityname", mDataset.get(position));
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}