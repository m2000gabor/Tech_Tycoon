package com.example.techtycoon;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyViewHolder> {

    class CompanyViewHolder extends RecyclerView.ViewHolder {
        private final TextView companyNameItemView;
        private final TextView companylastProfitItemView;
        private final TextView companyMoneyItemView;
        private final TextView companyValueItemView;

        private CompanyViewHolder(View itemView) {
            super(itemView);
            companyNameItemView = itemView.findViewById(R.id.companyNameTextView);
            companylastProfitItemView = itemView.findViewById(R.id.companyLastlySoldTextView);
            companyMoneyItemView = itemView.findViewById(R.id.companysMoneyTextView);
            companyValueItemView = itemView.findViewById(R.id.companysMarketValueTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Company> companies; // Cached copy of nameOfCompanies
    private View.OnClickListener mOnClickListener;

    public CompanyListAdapter(Context c, View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(c);
        mOnClickListener=onClickListener;
    }

    public Company getCompanyFromCache(int i){return companies.get(i);}



    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item_company, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new CompanyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
        if (companies != null) {
            Company current = companies.get(position);
            holder.companyNameItemView.setText(current.getCompanyName());
            holder.companylastProfitItemView.setText(String.format(Locale.getDefault(),"Last profit: %d$",current.lastProfit));
            holder.companyMoneyItemView.setText(String.format(Locale.getDefault(),"%d$",current.money));
            holder.companyValueItemView.setText(String.format(Locale.getDefault(),"Value: %,d$",current.getMarketValue()));
        } else {
            // Covers the case of data not being ready yet.
            holder.companyNameItemView.setText("Unknown");
        }
    }

    public void setCompanies(List<Company> companyList) {
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

