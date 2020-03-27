package com.example.techtycoon;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;


public class DeviceSmallListAdapter extends RecyclerView.Adapter<DeviceSmallListAdapter.DeviceSmallViewHolder> {

    private final LayoutInflater mInflater;
    private List<Device> devices; // Cached copy of devices
    private View.OnClickListener mOnClickListener;
    public DeviceSmallListAdapter(Context c, View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(c);
        mOnClickListener = onClickListener;
    }

    public Device getDeviceFromCache(int i) {
        return devices.get(i);
    }

    @Override
    public DeviceSmallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_small_device, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new DeviceSmallViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceSmallViewHolder holder, int position) {
        if (devices != null) {
            Device current = devices.get(position);
            holder.deviceNameItemView.setText(current.name);
            holder.deviceLastIncomeTextView.setText(String.format(Locale.getDefault(), "%d$", current.getSoldPieces() * current.profit));
            switch (current.getTrend()){
                case 1:holder.trendInIncomeImage.setImageResource(R.drawable.ic_trending_up_green_24dp);break;
                case 0:holder.trendInIncomeImage.setImageResource(R.drawable.ic_trending_flat_yellow_24dp);break;
                case -1:holder.trendInIncomeImage.setImageResource(R.drawable.ic_trending_down_red_24dp);break;
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.deviceNameItemView.setText("No device name");
            holder.deviceLastIncomeTextView.setText("?");
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

    class DeviceSmallViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameItemView;
        private final TextView deviceLastIncomeTextView;
        private final ImageView trendInIncomeImage;

        private DeviceSmallViewHolder(View itemView) {
            super(itemView);
            deviceNameItemView = itemView.findViewById(R.id.deviceNameTextView);
            deviceLastIncomeTextView = itemView.findViewById(R.id.incomeTextView);
            trendInIncomeImage = itemView.findViewById(R.id.trendInIncome);
        }
    }

}
