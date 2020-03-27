package com.example.techtycoon;

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
        private final TextView deviceIncomeTextView;
        private final TextView deviceStorageTextView;
        private final TextView deviceBodyTextView;
        private final TextView soldPiecesTextView;

        private DeviceViewHolder(View itemView) {
            super(itemView);
            deviceNameItemView = itemView.findViewById(R.id.deviceNameTextView);
            deviceIncomeTextView = itemView.findViewById(R.id.incomeTextView);
            deviceStorageTextView = itemView.findViewById(R.id.deviceStorageTextView);
            deviceBodyTextView = itemView.findViewById(R.id.deviceBodyTextView);
            soldPiecesTextView = itemView.findViewById(R.id.soldPiecesTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Device> devices; // Cached copy of devices
    private View.OnClickListener mOnClickListener;

    public DeviceListAdapter(Context c, View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(c);
        mOnClickListener=onClickListener;
    }

    public Device getDeviceFromCache(int i){return devices.get(i);}



    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_device, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        if (devices != null) {
            Device current = devices.get(position);
            holder.deviceNameItemView.setText(current.name);
            holder.deviceIncomeTextView.setText(String.format(Locale.getDefault(),"%d$",current.getSoldPieces()*current.profit));
            holder.deviceStorageTextView.setText(String.format(Locale.getDefault(),"Storage: %d",current.getScore_Storage()));
            holder.deviceBodyTextView.setText(String.format(Locale.getDefault(),"Body: %d",current.getScore_Body()));
            holder.soldPiecesTextView.setText(String.format(Locale.getDefault(),"Sold: %d",current.getSoldPieces()));
        } else {
            // Covers the case of data not being ready yet.
            holder.deviceNameItemView.setText("No device name");
            holder.deviceIncomeTextView.setText("0");
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
