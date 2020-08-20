package com.example.techtycoon.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.techtycoon.CompanyListAdapter;
import com.example.techtycoon.DeviceViewModel;
import com.example.techtycoon.R;
import com.example.techtycoon.ui.activities.charts.CompanyCharts;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FragmentStats extends Fragment {
        private DeviceViewModel deviceViewModel;
        private CompanyListAdapter adapter;

        public static FragmentStats newInstance() {
            FragmentStats fragment = new FragmentStats();
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
            View root =inflater.inflate(R.layout.fragment_stats, container, false);


            //general stats button
            root.findViewById(R.id.startMarketShareActivityButton).setOnClickListener(v -> {
                Intent intent=new Intent().setClass(getContext(), CompanyCharts.class);

                startActivity(intent);
            });



            return root;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        }

}

