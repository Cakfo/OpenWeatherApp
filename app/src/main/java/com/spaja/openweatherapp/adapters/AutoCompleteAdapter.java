package com.spaja.openweatherapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.spaja.openweatherapp.R;
import java.util.ArrayList;

/**
 * Created by Spaja on 19-Dec-16.
 */

public class AutoCompleteAdapter extends RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder> {
    private ArrayList<String> mDataset;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        EditText autoCompleteTextView;

        ViewHolder(View v) {
            super(v);
            cityName = (TextView) v.findViewById(R.id.city_name);
            autoCompleteTextView = (EditText) v.findViewById(R.id.et_city_name);
        }
    }

    public AutoCompleteAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public AutoCompleteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.autocomplete_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AutoCompleteAdapter.ViewHolder holder, final int position) {

        holder.cityName.setText(mDataset.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.autoCompleteTextView.setText(mDataset.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

