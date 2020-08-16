package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.techtycoon.dialogs.SortByDialog;
import com.example.techtycoon.ui.activities.FilterActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

public class FragmentAllDevices extends Fragment {

    private DeviceViewModel deviceViewModel;
    private DeviceListAdapter adapter;
    private LiveData<List<Device>> deviceList;
    private Spinner companySpinner;
    private boolean isDesc=true;
    private static int START_FILTER_ACTIVITY=123;
    public static String COMPANY_ID="companyId";
    public static String ATTRIBUTES="attr";

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

        //asc or desc button
        ImageButton imageButton=root.findViewById(R.id.ascOrDescImageView);
        imageButton.setOnClickListener(v->{deviceViewModel.setOrder(!isDesc); isDesc=!isDesc;});

        //Spinner spin = root.findViewById(R.id.sortbySpinner);
        root.findViewById(R.id.sortByImageView).setOnClickListener(v->showSortByDialog());

        //onclick
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //number of clicked item
                int itemPosition = recyclerView.getChildLayoutPosition(v);

                //get device fileds with mutable
                Device currentDev=adapter.getDeviceFromCache(itemPosition);
                int id = currentDev.id;

                //make intent
                Intent intent = new Intent();
                intent.putExtra("ID", id);
                intent.setClass(getContext(), DetailsOfOneDevice.class);
                //start new activity
                startActivity(intent);
            }
        };
        adapter = new DeviceListAdapter(getContext(), mOnClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        Observer<List<Device>> observer = new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> devs) {
                // Update the cached copy of the words in the adapter.
                adapter.setDevices(devs);
            }
        };
        deviceList.observe(getViewLifecycleOwner(), observer);

        //companySpinner=root.findViewById(R.id.companySpinner);
        //new SortBySpinnerAdapter(spin);
        //new CompanySpinnerAdapter();
        root.findViewById(R.id.filterByButton).setOnClickListener(v -> {
            Intent intent=new Intent(getContext(),FilterActivity.class);
            startActivityForResult(intent,START_FILTER_ACTIVITY);
        });


        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==START_FILTER_ACTIVITY && resultCode==RESULT_OK){
            ArrayList<Integer> companyIds=data.getIntegerArrayListExtra(COMPANY_ID);
            deviceViewModel.filterBy(Device.DeviceAttribute.OWNER_ID, companyIds);

            ArrayList<String> attributesAsString=data.getStringArrayListExtra(ATTRIBUTES);
            for(int i=0;i<attributesAsString.size();i++){
                ArrayList<String> attributeValuesAsString=data.getStringArrayListExtra(attributesAsString.get(i));
                ArrayList<Integer> attributeValues=new ArrayList<>();
                for (String str :
                        attributeValuesAsString) {
                    attributeValues.add(Integer.parseInt(str));
                }
                deviceViewModel.filterBy(Device.DeviceAttribute.valueOf(attributesAsString.get(i)),
                        attributeValues);
            }
        }
    }

    //set up sort by button
    private void showSortByDialog(){
        DialogFragment dialog=new SortByDialog();
        dialog.show(getChildFragmentManager(),"cloneDevice");
    }

    void sortBy(Device.DeviceAttribute code)
    {deviceViewModel.orderDevices_ByCode2(code,isDesc);}


    //set up the sortby spinner
    public class SortBySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {
        Spinner spin;

        SortBySpinnerAdapter(Spinner sp) {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            this.spin =sp;
            spin.setOnItemSelectedListener(this);
            String[] sortingOptions = {"SortBy","Name","ID","Performance","Storage","Body","Price","Income","Sold pieces","Profit per item",
                    "Ram", "Memory","Design","Material","Color","IP","Bezels"};

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,sortingOptions );
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            if(position==0 || position==1){
                deviceViewModel.orderDevices_ByCode2(Device.DeviceAttribute.NAME,isDesc);
            }else{
                deviceViewModel.orderDevices_ByCode2(Device.getAllAttribute().get(position-10),isDesc);}
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
            deviceViewModel.filterBy(Device.DeviceAttribute.OWNER_ID,Collections.singletonList(companyID));
            //deviceList=deviceViewModel.filter_byCompanyID(companyID);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

}
