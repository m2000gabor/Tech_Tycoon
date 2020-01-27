package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//TODO using livedata and observer appropriately

public class AllDevices extends AppCompatActivity{
    private DeviceViewModel deviceViewModel;
    private DeviceListAdapter adapter;
    LiveData<List<Device>> deviceList;
    Observer<List<Device>> observer;
    Spinner companySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.devicesRecyclerView);


            // Get a new or existing ViewModel from the ViewModelProvider.
            deviceViewModel =new ViewModelProvider(this).get(DeviceViewModel.class);
            deviceList = deviceViewModel.mutable_getAllDevices();


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
                    int ram = currentDev.ram;
                    int memory = currentDev.memory;
                    int cost = currentDev.cost;


                    /*
                    //get device fields
                    String nev = Objects.requireNonNull(deviceList.getValue()).get(itemPosition).name;
                    int id = deviceList.getValue().get(itemPosition).id;
                    int price = deviceList.getValue().get(itemPosition).getPrice();
                    int profit = deviceList.getValue().get(itemPosition).profit;
                    int companyId = deviceList.getValue().get(itemPosition).ownerCompanyId;
                    int ram = deviceList.getValue().get(itemPosition).ram;
                    int memory = deviceList.getValue().get(itemPosition).memory;
                    int cost = deviceList.getValue().get(itemPosition).cost;*/



                    //make intent
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.NAME_FIELD, nev);
                    intent.putExtra(MainActivity.DEVICE_PRICE, price);
                    intent.putExtra(MainActivity.MAIN_MONETARIAL_INFO, profit);
                    intent.putExtra(MainActivity.DEVICE_COMPANY_ID, companyId);
                    intent.putExtra(MainActivity.DEVICE_RAM, ram);
                    intent.putExtra(MainActivity.DEVICE_MEMORY, memory);
                    intent.putExtra(MainActivity.DEVICE_COST, cost);
                    intent.putExtra("ID", id);
                    intent.setClass(getBaseContext(), DetailsOfOneDevice.class);

                    //start new activity
                    startActivityForResult(intent, MainActivity.DISPLAY_DEVICES_REQUEST_CODE);
                }
            };
            adapter = new DeviceListAdapter(this, mOnClickListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            // Add an observer on the LiveData returned by getAlphabetizedWords.
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.
            observer=new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable final List<Device> devs) {
                // Update the cached copy of the words in the adapter.
                adapter.setDevices(devs);
            }};
            deviceList.observe(this,observer);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        companySpinner=findViewById(R.id.companySpinner);
        new CompanySpinnerAdapter();
        new SortBySpinnerAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuitem_deleteALL) {
            deviceViewModel.deleteAll();
            return true;
        }else if(id == R.id.action_sortBy){
            //adapter.setDevices(deviceViewModel.getDeviceList_orderedBy_SoldPieces());
            //
            //            showNoticeDialog();
            //            //sortBy();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data.getBooleanExtra("IS_DELETE",false)) {
            deviceViewModel.delOneDeviceById(data.getIntExtra("ID",-1));
            Toast.makeText(getApplicationContext(), "SIKERULT torolni", Toast.LENGTH_LONG).show();
        }
    }


    //set up the sortby spinner
    public class SortBySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {

        SortBySpinnerAdapter() {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            Spinner spin = findViewById(R.id.sortbySpinner);
            spin.setOnItemSelectedListener(this);
            String[] sortingOptions = {"ID","Sold pieces", "Ram", "Memory"};

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,sortingOptions );
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
            deviceViewModel.orderDevices_ByCode(position);
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
            ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,
                 namesOfCompanies );
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            companySpinner.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            observer=new Observer<List<Device>>() {
                @Override
                public void onChanged(@Nullable final List<Device> devs) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setDevices(devs);
                }};
            if(position==0){deviceList=deviceViewModel.getAllDevices();
            }else{
                int[] companyIDs={companies[position-1].companyId};
                deviceList=deviceViewModel.filter_byCompanyIDs(companyIDs);
            }

            deviceList.observeForever(observer);
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

}
