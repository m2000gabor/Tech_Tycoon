package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

//TODO separate allDevices and allCompanies class
//TODO sort by

public class AllDevices extends AppCompatActivity {
    private DeviceViewModel deviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_devices);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        /// determine the request (what should we display)
        int requestCode=getIntent().getIntExtra(MainActivity.TASK_OF_RECYCLER_VIEW,0);


        if(requestCode==MainActivity.DISPLAY_COMPANIES_REQUEST_CODE){//list nameOfCompanies
            // Get a new or existing ViewModel from the ViewModelProvider.
            deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
            LiveData<List<Company>> companies = deviceViewModel.getAllCompanies();

            //onclick
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //number of clicked item
                    int itemPosition = recyclerView.getChildLayoutPosition(v);

                    //get device fields
                    String nev = Objects.requireNonNull(companies.getValue()).get(itemPosition).name;
                    int id = companies.getValue().get(itemPosition).companyId;
                    int money = companies.getValue().get(itemPosition).money;

                    //make intent
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.NAME_FIELD, nev);
                    intent.putExtra(MainActivity.MAIN_MONETARIAL_INFO, money);
                    intent.putExtra("ID", id);
                    intent.setClass(getBaseContext(), DetailsOfOneCompany.class);

                    //start new activity
                    startActivityForResult(intent, requestCode);
                }
            };
            final CompanyListAdapter adapter = new CompanyListAdapter(this, mOnClickListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            // Add an observer on the LiveData returned by getAlphabetizedWords.
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.

            deviceViewModel.getAllCompanies().observe(this, new Observer<List<Company>>() {
                @Override
                public void onChanged(@Nullable final List<Company> comps) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setCompanies(comps);
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        }else { //otherwise list all devices
            // Get a new or existing ViewModel from the ViewModelProvider.
            deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
            LiveData<List<Device>> devices = deviceViewModel.getAllDevices();


            //onclick
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //number of clicked item
                    int itemPosition = recyclerView.getChildLayoutPosition(v);

                    //get device fields
                    String nev = Objects.requireNonNull(devices.getValue()).get(itemPosition).name;
                    int id = devices.getValue().get(itemPosition).id;
                    int price = devices.getValue().get(itemPosition).getPrice();
                    int profit = devices.getValue().get(itemPosition).profit;
                    int companyId = devices.getValue().get(itemPosition).ownerCompanyId;
                    int ram = devices.getValue().get(itemPosition).ram;
                    int memory = devices.getValue().get(itemPosition).memory;
                    int cost = devices.getValue().get(itemPosition).cost;



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
            final DeviceListAdapter adapter = new DeviceListAdapter(this, mOnClickListener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            // Add an observer on the LiveData returned by getAlphabetizedWords.
            // The onChanged() method fires when the observed data changes and the activity is
            // in the foreground.
            deviceViewModel.getAllDevices().observe(this, new Observer<List<Device>>() {
                @Override
                public void onChanged(@Nullable final List<Device> devs) {
                    // Update the cached copy of the words in the adapter.
                    adapter.setDevices(devs);
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data.getBooleanExtra("IS_DELETE",false)) {
            switch (requestCode){
                case MainActivity.DISPLAY_DEVICES_REQUEST_CODE:
                    deviceViewModel.delOneDeviceById(data.getIntExtra("ID",-1));
                    break;
                case MainActivity.NEW_COMPANY_ACTIVITY_REQUEST_CODE:
                    deviceViewModel.delOneCompanyById(data.getIntExtra("ID",-1));
                    break;
            }
            Toast.makeText(getApplicationContext(), "SIKERULT torolni", Toast.LENGTH_LONG).show();
        }
    }

}
