package com.example.techtycoon;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyViewHolder> {

    class CompanyViewHolder extends RecyclerView.ViewHolder {
        private final TextView companyNameItemView;

        private CompanyViewHolder(View itemView) {
            super(itemView);
            companyNameItemView = itemView.findViewById(R.id.deviceNameTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Company> companies; // Cached copy of nameOfCompanies
    private View.OnClickListener mOnClickListener;

    CompanyListAdapter(Context c,View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(c);
        mOnClickListener=onClickListener;
    }



    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new CompanyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
        if (companies != null) {
            Company current = companies.get(position);
            holder.companyNameItemView.setText(current.getCompanyName());
        } else {
            // Covers the case of data not being ready yet.
            holder.companyNameItemView.setText("No Word");
        }
    }

    void setCompanies(List<Company> companyList) {
        companies = companyList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // devices has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (companies != null)
            return companies.size();
        else return 0;
    }

}

