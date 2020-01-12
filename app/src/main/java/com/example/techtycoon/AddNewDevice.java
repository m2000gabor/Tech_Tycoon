package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class AddNewDevice extends AppCompatActivity {


    private EditText deviceNameField;
    private EditText profitField;
    String[] nameOfCompanies;
    List<Company> companies;
    DeviceViewModel deviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //find fields
        deviceNameField = findViewById(R.id.deviceNameInputField);
        profitField = findViewById(R.id.profitInputField);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDevice(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
        companies=deviceViewModel.getAllCompaniesList();

        String[] tmp=new String[companies.size()];
        for(int i=0;i<companies.size();i++){tmp[i]=companies.get(i).name;}
        nameOfCompanies =tmp;
        new MySpinnerAdapter();

    }

    protected void addDevice(View view) {

        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(deviceNameField.getText()) || TextUtils.isEmpty(profitField.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            String deviceName = deviceNameField.getText().toString();
            int profit = Integer.parseInt(profitField.getText().toString());
            Spinner sp=findViewById(R.id.spinner);
            int maker=companies.get(sp.getSelectedItemPosition()).companyId;

            deviceViewModel.insertDevice(new Device(deviceName,profit,maker));

            /*replyIntent.putExtra(MainActivity.NAME_FIELD, deviceName);
            replyIntent.putExtra(MainActivity.MAIN_MONETARIAL_INFO, profit);*/
            setResult(RESULT_OK, replyIntent);
        }
        finish();
    }

    public class MySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {



        public MySpinnerAdapter() {
            //Getting the instance of Spinner and applying OnItemSelectedListener on it
            Spinner spin = (Spinner) findViewById(R.id.spinner);
            spin.setOnItemSelectedListener(this);

            //Creating the ArrayAdapter instance having the nameOfCompanies list
            ArrayAdapter aa = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item, nameOfCompanies);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
        }

        //Performing action onItemSelected and onNothing selected
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
            //Toast.makeText(getApplicationContext(), nameOfCompanies[position] , Toast.LENGTH_LONG).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
