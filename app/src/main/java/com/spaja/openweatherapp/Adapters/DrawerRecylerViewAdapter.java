package com.spaja.openweatherapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spaja.openweatherapp.R;

import static com.spaja.openweatherapp.R.styleable.View;

/**
 * Created by Spaja on 14-Dec-16.
 */

class DrawerRecylerViewAdapter extends RecyclerView.Adapter<DrawerRecylerViewAdapter.ViewHolder> {

    private String[] mDataset;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        ViewHolder(android.view.View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_view_item);
        }
    }

    public DrawerRecylerViewAdapter(String[] dataSet) {
        mDataset = dataSet;
    }

    @Override
    public DrawerRecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
        DrawerRecylerViewAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DrawerRecylerViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}