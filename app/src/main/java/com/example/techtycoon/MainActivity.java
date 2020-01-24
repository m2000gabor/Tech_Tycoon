package com.example.techtycoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//TODO time
//TODO companies have a development route and cost limiting the available features
//TODO detailed stats

public class MainActivity extends AppCompatActivity {
    public static final String NAME_FIELD ="name" ;
    public static final String MAIN_MONETARIAL_INFO ="profit" ;
    public static final String TASK_OF_RECYCLER_VIEW ="dataSource" ;
    public static final String DEVICE_COMPANY_ID ="companyId" ;
    public static final String DEVICE_PRICE ="price" ;
    public static final String DEVICE_RAM ="ram" ;
    public static final String DEVICE_MEMORY ="memory" ;
    public static final String DEVICE_COST ="cost" ;


    public static final int DISPLAY_DEVICES_REQUEST_CODE =1;
    public static final int DISPLAY_COMPANIES_REQUEST_CODE =2;
    public static final int NEW_DEVICE_ACTIVITY_REQUEST_CODE = 1; //now independent from mainactivity
    public static final int NEW_COMPANY_ACTIVITY_REQUEST_CODE = 2;

    DeviceViewModel deviceViewModel;
    Simulator simulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddNewDeviceActivity();
            }
        });

        //create db
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        // Get a new or existing ViewModel from the ViewModelProvider.
        deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);

        //get sharedPrefs
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        float lastAvgPrice = sharedPref.getFloat(getString(R.string.simulator_lastAvgPrice), 5);
        simulator=new Simulator(deviceViewModel,lastAvgPrice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuitem_add_a_company) {
            startAddNewCompanyActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the List Items button */
    public void listItems(View view) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),AllDevices.class);
        intent.putExtra(TASK_OF_RECYCLER_VIEW,DISPLAY_DEVICES_REQUEST_CODE);
        startActivity(intent);
    }

    public void listCompanies(View view){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),AllDevices.class);
        intent.putExtra(TASK_OF_RECYCLER_VIEW,DISPLAY_COMPANIES_REQUEST_CODE);
        startActivity(intent);
    }

    public void start_again(View view){
        deviceViewModel.deleteAll();
        deviceViewModel.insertCompany(new Company("Apple",10));
        deviceViewModel.insertCompany(new Company("Samsung",10));
        deviceViewModel.insertCompany(new Company("Xiaomi",10));
        deviceViewModel.insertCompany(new Company("Sony",10));
        deviceViewModel.insertCompany(new Company("MyPhone",10));
    }

    public void start_simulation(View view){
        simulator.simulate();
        float lastAvgPrice=(float) simulator.lastAvgPrice;
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getString(R.string.simulator_lastAvgPrice), lastAvgPrice);
        editor.apply();
        Toast.makeText(getApplicationContext(), "1 month simulated", Toast.LENGTH_SHORT).show();
    }

    public void startAddNewDeviceActivity(){
        Intent addNewDevice = new Intent(getApplicationContext(), DeviceCreator.class);
        startActivityForResult(addNewDevice,NEW_DEVICE_ACTIVITY_REQUEST_CODE);
    }

    public void startAddNewCompanyActivity(){
        Intent addNewCompany = new Intent(getApplicationContext(),AddNewCompany.class);
        startActivityForResult(addNewCompany,NEW_COMPANY_ACTIVITY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case NEW_DEVICE_ACTIVITY_REQUEST_CODE:
                    //now independent from mainactivity
                    break;
                case NEW_COMPANY_ACTIVITY_REQUEST_CODE:
                    String name=data.getStringExtra(NAME_FIELD);
                    int monetary=data.getIntExtra(MAIN_MONETARIAL_INFO,0);
                    Company c = new Company(name,monetary);
                    deviceViewModel.insertCompany(c);
                    Toast.makeText(
                            getApplicationContext(),
                            "SIKERULT hozzaadni",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "NEM sikerult hozzaadni",
                    Toast.LENGTH_LONG).show();
        }
    }
}
