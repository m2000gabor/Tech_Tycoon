package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

//TODO time
//TODO companies have a development route and cost limiting the available features
//TODO detailed stats
//todo store the discontinued devices
//todo release time for companies (now the new devices come too frequent)

public class MainActivity extends AppCompatActivity {
    public static final String NAME_FIELD ="name" ;
    public static final String MAIN_MONETARIAL_INFO ="profit" ;
    public static final String TASK_OF_RECYCLER_VIEW ="dataSource" ;
    public static final String DEVICE_COMPANY_ID ="companyId" ;
    public static final String DEVICE_PRICE ="price" ;
    public static final String DEVICE_RAM ="ram" ;
    public static final String DEVICE_MEMORY ="memory" ;
    public static final String DEVICE_COST ="cost" ;
    public static final String DEVICE_BODY ="body" ;
    public static final String DEVICE_PARAMS ="devParams" ;
    public static final String RAM_LVL ="RAMLVL" ;
    public static final String MEMORY_LVL ="MEMLVL" ;
    public static final String LEVELS ="LEVELS" ;


    public static final int DISPLAY_DEVICES_REQUEST_CODE =1;
    public static final int DISPLAY_COMPANIES_REQUEST_CODE =2;
    public static final int NEW_DEVICE_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_COMPANY_ACTIVITY_REQUEST_CODE = 2;

    private static final int[] STARTING_LEVELS={1,1,1,1,1,1,1};

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
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user touches the List Devices button */
    public void deleteAll(View view) {
        deviceViewModel.deleteAll();
    }

    public void addCompany(View view){startAddNewCompanyActivity();}

    public void start_again(View view){
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
                    int monetary=data.getIntExtra(MAIN_MONETARIAL_INFO,0);
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
}
