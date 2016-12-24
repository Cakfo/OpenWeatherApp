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


public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> mDataset;
    private DrawerLayout mDrawerLayout;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    public DrawerRecyclerViewAdapter(ArrayList<String> mDataset, DrawerLayout mDrawerLayout) {
        this.mDataset = mDataset;
        this.mDrawerLayout = mDrawerLayout;
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder{

        TextView textView;
        ViewHolderItem(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.list_view_tv);
        }
    }

    private class ViewHolderHeader extends RecyclerView.ViewHolder{

        ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_header, parent, false);
            return new ViewHolderHeader(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
            return new ViewHolderItem(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItem) {
            ((ViewHolderItem) holder).textView.setText(mDataset.get(position));
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
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }
}