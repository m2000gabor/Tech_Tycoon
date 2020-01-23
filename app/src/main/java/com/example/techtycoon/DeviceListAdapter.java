package com.example.techtycoon;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameItemView;
        private final TextView deviceLastlySoldTextView;

        private DeviceViewHolder(View itemView) {
            super(itemView);
            deviceNameItemView = itemView.findViewById(R.id.deviceNameTextView);
            deviceLastlySoldTextView = itemView.findViewById(R.id.deviceLastlySoldTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Device> devices; // Cached copy of devices
    private View.OnClickListener mOnClickListener;

    DeviceListAdapter(Context c,View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(c);
        mOnClickListener=onClickListener;
    }



    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        if (devices != null) {
            Device current = devices.get(position);
            holder.deviceNameItemView.setText(current.name);
            holder.deviceLastlySoldTextView.setText(String.format(Locale.getDefault(),"%d",current.soldPieces));
        } else {
            // Covers the case of data not being ready yet.
            holder.deviceNameItemView.setText("No device name");
            holder.deviceLastlySoldTextView.setText("0");
        }
    }

    void setDevices(List<Device> deviceList) {
        devices = deviceList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // devices has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (devices != null)
            return devices.size();
        else return 0;
    }

}
