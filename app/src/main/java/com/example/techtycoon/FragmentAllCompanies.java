package com.example.techtycoon;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentAllCompanies extends Fragment {
    final static int[] sharedPrefKeyIds={R.string.simulator_lastAvgPrice,R.string.simulator_lastAvgRam,
            R.string.simulator_lastAvgMemory,R.string.simulator_lastAvgDesign,R.string.simulator_lastAvgMaterial,
            R.string.simulator_lastAvgColors,R.string.simulator_lastAvgIp,R.string.simulator_lastAvgBezels,R.string.turn_counter};
    static final String GENERAL_STATS="generalStats";
    private DeviceViewModel deviceViewModel;
    private CompanyListAdapter adapter;

    public static FragmentAllCompanies newInstance() {
        FragmentAllCompanies fragment = new FragmentAllCompanies();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =inflater.inflate(R.layout.fragment_all_companies, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.companiesRecyclerView);

        //onclick
        View.OnClickListener mOnClickListener = v -> {
            //number of clicked item
            int itemPosition = recyclerView.getChildLayoutPosition(v);
            Company current=adapter.getCompanyFromCache(itemPosition);

            //get device fields
            int id = current.companyId;

            //make intent
            Intent intent = new Intent();
            intent.putExtra("ID", id);
            intent.setClass(getContext(), DetailsOfOneCompany.class);

            //start new activity
            startActivityForResult(intent, MainActivity.DISPLAY_COMPANIES_REQUEST_CODE);
        };
        adapter = new CompanyListAdapter(getContext(), mOnClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //general stats button
        root.findViewById(R.id.startGeneralStatisticActivity).setOnClickListener(v -> {
            Intent intent=new Intent().setClass(getContext(),GeneralStatsActivity.class);
            SharedPreferences sharedPref=requireActivity().getPreferences(Context.MODE_PRIVATE);
            ArrayList<String> values=new ArrayList<>();
            for (int sharedPrefKeyId : sharedPrefKeyIds) {
                values.add(String.format(Locale.getDefault(), "%.2f",
                        sharedPref.getFloat(getString(sharedPrefKeyId), -1)));
            }
            intent.putStringArrayListExtra(GENERAL_STATS,values);
            startActivity(intent);
        });

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

        deviceViewModel.getAllCompanies().observe(getViewLifecycleOwner(), new Observer<List<Company>>() {
            @Override
            public void onChanged(@Nullable final List<Company> comps) {
                // Update the cached copy of the words in the adapter.
                adapter.setCompanies(comps);
            }
        });

        return root;
    }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        }
}
