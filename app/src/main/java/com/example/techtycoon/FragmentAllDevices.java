package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import static android.app.Activity.RESULT_OK;


public class FragmentAllDevices extends Fragment {
    //todo sort by price

    private DeviceViewModel deviceViewModel;
    private DeviceListAdapter adapter;
    private LiveData<List<Device>> deviceList;
    private Observer<List<Device>> observer;
    private Spinner companySpinner;

    public static FragmentAllDevices newInstance() {
        FragmentAllDevices fragment = new FragmentAllDevices();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a new or existing ViewModel from the ViewModelProvider.
        deviceViewModel =new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceList = deviceViewModel.mutable_getAllDevices();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_devices, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.devicesRecyclerView);
        Spinner spin = root.findViewById(R.id.sortbySpinner);
        //onclick
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //number of clicked item
                int itemPosition = recyclerView.getChildLayoutPosition(v);

                //get device fileds with mutable
                Device currentDev=adapter.getDeviceFromCache(itemPosition);
                String nev = currentDev.name;
                int id = currentDev.id;
                int price =currentDev.getPrice();
                int profit = currentDev.profit;
                int companyId = currentDev.ownerCompanyId;
                int cost = currentDev.cost;
                int[][] devParams = currentDev.getParams();


                //make intent
                Intent intent = new Intent();
                intent.putExtra(MainActivity.NAME_FIELD, nev);
                intent.putExtra(MainActivity.DEVICE_PRICE, price);
                intent.putExtra(MainActivity.MAIN_MONETARIAL_INFO, profit);
                intent.putExtra(MainActivity.DEVICE_COMPANY_ID, companyId);
                intent.putExtra(MainActivity.DEVICE_COST, cost);
                intent.putExtra(MainActivity.DEVICE_PARAMS, Device.mtxToArray(devParams));
                intent.putExtra("ID", id);
                intent.setClass(getContext(), DetailsOfOneDevice.class);

                //start new activity
                startActivityForResult(intent, MainActivity.DISPLAY_DEVICES_REQUEST_CODE);
            }
        };
        adapter = new DeviceListAdapter(getContext(), mOnClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        observer=new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> devs) {
                // Update the cached copy of the words in the adapter.
                adapter.setDevices(devs);
            }};
        deviceList.observe(getViewLifecycleOwner(),observer);

        companySpinner=root.findViewById(R.id.companySpinner);
        new SortBySpinnerAdapter(spin);
        new CompanySpinnerAdapter();


        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getBooleanExtra("IS_DELETE",false)) {
            deviceViewModel.delOneDeviceById(data.getIntExtra("ID",-1));
            Toast.makeText(getContext(), "SIKERULT torolni", Toast.LENGTH_LONG).show();
        }
    }

    //set up the sortby spinner
    public class SortBySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {
        Spinner spin;

        SortBySpinnerAdapter(Spinner sp) {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            this.spin =sp;
            spin.setOnItemSelectedListener(this);
            String[] sortingOptions = {"SortBy","ID","Sold pieces", "Ram", "Memory","Profit per item","Name","Price","Overall income"};

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,sortingOptions );
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            //Toast.makeText(getApplicationContext(),Boolean.toString(deviceList.hasObservers()), Toast.LENGTH_LONG).show();

            // Update the cached copy of the words in the adapter.
            //observer= devs -> { adapter.setDevices(devs); };
            if(position==0){position++;}
            deviceViewModel.orderDevices_ByCode2(position-1);
            //Toast.makeText(getApplicationContext(),Boolean.toString(deviceList.hasObservers()), Toast.LENGTH_LONG).show();
            //deviceList.observeForever(observer);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }


    //set up the company spinner
    public class CompanySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {
        Company[] companies;

        CompanySpinnerAdapter() {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            companySpinner.setOnItemSelectedListener(this);
            List<Company> companyList=deviceViewModel.getAllCompaniesList();
            companies=new Company[companyList.size()];
            companies=companyList.toArray(companies);
            String[] namesOfCompanies=new String[1+companyList.size()];
            namesOfCompanies[0]="From all Companies";
            for (int i=1;i<companyList.size()+1;++i){namesOfCompanies[i]=companies[i-1].name;}


            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,
                    namesOfCompanies );
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            companySpinner.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            position--;
            int companyID;
            if(position>=0){companyID=companies[position].companyId;
            }else{companyID=-1;}
            deviceViewModel.filter_byCompanyID(companyID);
            //deviceList=deviceViewModel.filter_byCompanyIDs(companyIDs);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

}
