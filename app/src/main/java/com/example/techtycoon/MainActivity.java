package com.example.techtycoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

//TODO time
//TODO detailed stats
//todo store the discontinued devices
//todo release time for companies (now the new devices come too frequent)
//todo stats
//todo campaign
//todo cheater bot

public class MainActivity extends AppCompatActivity {
    public static final String NAME_FIELD ="name" ;
    public static final String MAIN_MONETARILY_INFO ="profit" ;
    public static final String TASK_OF_RECYCLER_VIEW ="dataSource" ;
    public static final String DEVICE_COMPANY_ID ="companyId" ;
    public static final String DEVICE_PRICE ="price" ;
    public static final String DEVICE_COST ="cost" ;
    public static final String DEVICE_PARAMS ="devParams" ;
    public static final String RAM_LVL ="RAMLVL" ;
    public static final String MEMORY_LVL ="MEMLVL" ;
    public static final String LEVELS ="LEVELS" ;


    public static final int DISPLAY_DEVICES_REQUEST_CODE =1;
    public static final int DISPLAY_COMPANIES_REQUEST_CODE =2;
    public static final int NEW_DEVICE_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_COMPANY_ACTIVITY_REQUEST_CODE = 2;

    public static final int[] STARTING_LEVELS={1,1,1,1,1,1,1};

    DeviceViewModel deviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //create db
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        // Get a new or existing ViewModel from the ViewModelProvider.
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
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
        }else if(id== R.id.menuitem_deleteALL){
            deviceViewModel.deleteAll();
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the List Devices button */
    public void nothing(View view) {}

    public void populateForTest(View view){
        resetSharedPref();
        //make levels
        int[] attributes=new int[Device.NUMBER_OF_ATTRIBUTES];
        int[] levels=new int[Device.NUMBER_OF_ATTRIBUTES];
        for (int i = 0; i < Device.NUMBER_OF_ATTRIBUTES; i++) {
            attributes[i]=2;
            levels[i]=3;
        }

        //make the companies
        Company c1=new Company("admin",1,levels);
        c1.companyId=1;
        c1.maxSlots=10;
        c1.money=10000000;
        deviceViewModel.startAgain(c1);

        //make the devices
        List<Device> deviceList=new LinkedList<>();
        //átlag
        Device d1=new Device("átlag",100,DeviceValidator.getOverallCost(attributes),1,attributes);
        Device d2=new Device("átlag2",100,DeviceValidator.getOverallCost(attributes),1,attributes);

        //erősek
        attributes[0]++;
        attributes[4]++;
        Device d3=new Device("csúcs",100,DeviceValidator.getOverallCost(attributes),1,attributes);
        Device d4=new Device("csúcs drágán",200,DeviceValidator.getOverallCost(attributes),1,attributes);
        Device d5=new Device("csúcs olcsón",70,DeviceValidator.getOverallCost(attributes),1,attributes);

        //gyengek
        attributes[0]--;
        attributes[0]--;
        attributes[4]--;
        Device d6=new Device("kicsit gy",100,DeviceValidator.getOverallCost(attributes),1,attributes);
        Device d7=new Device("kicsit gy olcs",70,DeviceValidator.getOverallCost(attributes),1,attributes);
        attributes[4]--;
        attributes[5]--;
        Device d8=new Device("nagyon gy",100,DeviceValidator.getOverallCost(attributes),1,attributes);
        Device d9=new Device("nagyon gy olcs",70,DeviceValidator.getOverallCost(attributes),1,attributes);


        deviceList.add(d1);
        deviceList.add(d2);
        deviceList.add(d3);
        deviceList.add(d4);
        deviceList.add(d5);
        deviceList.add(d6);
        deviceList.add(d7);
        deviceList.add(d8);
        deviceList.add(d9);
        c1.usedSlots=deviceList.size();
        deviceViewModel.updateCompanies(c1);
        deviceViewModel.insertDevices(deviceList.toArray(new Device[0]));
    }

    public void start_again(View view){
        resetSharedPref();
        Company[] companies={
                new Company("Apple",10,STARTING_LEVELS),
                new Company("Samsung",10,STARTING_LEVELS),
                new Company("Xiaomi",10,STARTING_LEVELS),
                new Company("Sony",10,STARTING_LEVELS),
                new Company("MyPhone",10,STARTING_LEVELS),
                new Company("Admin",1000000000,STARTING_LEVELS)};

        deviceViewModel.startAgain(companies);
    }
    public void start_again_bots(View view){
        resetSharedPref();
        Company[] companies={
                new Company("Apple",1,STARTING_LEVELS),
                new Company("Strawberry",1,STARTING_LEVELS),
                new Company("Samsung",1,STARTING_LEVELS),
                new Company("Sony",1,STARTING_LEVELS),
                new Company("Player",1,STARTING_LEVELS)};
        deviceViewModel.startAgain(companies);
    }

    public void startAddNewCompanyActivity(){
        Intent addNewCompany = new Intent(getApplicationContext(),AddNewCompany.class);
        startActivityForResult(addNewCompany,NEW_COMPANY_ACTIVITY_REQUEST_CODE);
    }

    public void startTabbedActivity(View view){
        startActivity(new Intent().setClass(this,TabbedActivity.class));
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
                    int monetary=data.getIntExtra(MAIN_MONETARILY_INFO,0);
                    Company c = new Company(name,monetary,STARTING_LEVELS);
                    deviceViewModel.insertCompanies(c);
                    Toast.makeText(
                            getApplicationContext(),
                            "SIKERULT hozzaadni",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }else{
            Toast.makeText(getApplicationContext(),"Result is not OK",Toast.LENGTH_LONG).show();
        }
    }

    void resetSharedPref(){
        //todo make a global sharedpref handler
    }
}
