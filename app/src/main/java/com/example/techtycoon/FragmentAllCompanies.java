package com.example.techtycoon;

import android.content.Intent;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import static android.app.Activity.RESULT_OK;


public class FragmentAllCompanies extends Fragment {
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
        LiveData<List<Company>> companies = deviceViewModel.getAllCompanies();


        //onclick
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //number of clicked item
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                Company current=adapter.getCompanyFromCache(itemPosition);

                //get device fields
                String nev = current.name;
                int id = current.companyId;
                int money = current.money;
                int[] levels=current.getLevels_USE_THIS();

                //make intent
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NAME_FIELD, nev);
                intent.putExtra(MainActivity.MAIN_MONETARIAL_INFO, money);
                intent.putExtra("ID", id);
                intent.putExtra(MainActivity.LEVELS,levels);
                intent.setClass(getContext(), DetailsOfOneCompany.class);

                //start new activity
                startActivityForResult(intent, MainActivity.DISPLAY_COMPANIES_REQUEST_CODE);
            }
        };
        adapter = new CompanyListAdapter(getContext(), mOnClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


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
            /*
            if (resultCode == RESULT_OK && data.getBooleanExtra("IS_DELETE",false)) {
                deviceViewModel.delOneCompanyById(data.getIntExtra("ID",-1));
                Toast.makeText(getContext(), "SIKERULT torolni", Toast.LENGTH_LONG).show();
            }else if(resultCode==RESULT_OK && data.getBooleanExtra("IS_UPDATE",false)){
                int id =data.getIntExtra("ID",-1);
                int[] levels= data.getIntArrayExtra(MainActivity.LEVELS);
                int money =data.getIntExtra(MainActivity.MAIN_MONETARIAL_INFO,-1);
                if(levels.length >0 && money !=-1){
                    deviceViewModel.updateLevel(id, levels,money);
                }
            }*/
        }
}
