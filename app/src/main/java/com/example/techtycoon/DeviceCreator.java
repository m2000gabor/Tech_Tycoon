package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DeviceCreator extends AppCompatActivity {

    private static final int CHOOSE_MEMORY_REQUEST = 1;
    boolean isMemorySet=false;
    int ram;
    int memory;
    int memoryCost=0;

    //from newdeviceBasics
    private EditText deviceNameField;
    private EditText profitField;
    private TextView currentCostField;
    private ImageView isSetMemoryImage;
    String[] nameOfCompanies;
    List<Company> companies;
    DeviceViewModel deviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_creator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //find fields
        deviceNameField = findViewById(R.id.deviceNameInputField);
        profitField = findViewById(R.id.profitInputField);
        currentCostField = findViewById(R.id.currentCostTextView);
        isSetMemoryImage = findViewById(R.id.isSetMemory);


        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        companies=deviceViewModel.getAllCompaniesList();

        String[] tmp=new String[companies.size()];
        for(int i=0;i<companies.size();i++){tmp[i]=companies.get(i).name;}
        nameOfCompanies =tmp;
        new MySpinnerAdapter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMemorySet){
                    addDevice();
                }else {
                    Toast.makeText(getApplicationContext(),"All parameters have to be specified!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    public void startMemoryChooser(View view){
        Intent chooseMemory = new Intent(getApplicationContext(),ChooseMemoryActivity.class);
        startActivityForResult(chooseMemory,CHOOSE_MEMORY_REQUEST);
    }

    protected void addDevice() {
        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(deviceNameField.getText()) || TextUtils.isEmpty(profitField.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            String deviceName = deviceNameField.getText().toString();
            int profit = Integer.parseInt(profitField.getText().toString());
            Spinner sp=findViewById(R.id.spinner);
            int maker=companies.get(sp.getSelectedItemPosition()).companyId;

            deviceViewModel.insertDevice(new Device(deviceName,profit, getOverallCost(),maker,ram,memory));

            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CHOOSE_MEMORY_REQUEST:
                    ram=data.getIntExtra("amountOfRam",0);
                    memory=data.getIntExtra("amountOfMemory",0);
                    memoryCost=data.getIntExtra("costs",99);
                    currentCostField.setText(String.format(Locale.getDefault(),"The current cost is %d$", getOverallCost()));
                    //Toast.makeText(this,Integer.toString(cost),Toast.LENGTH_LONG).show();
                    isMemorySet=true;
                    isSetMemoryImage.setImageDrawable(getDrawable(R.drawable.ic_check_green_24dp));
                    break;
            }
        }
    }

    public class MySpinnerAdapter implements
            AdapterView.OnItemSelectedListener {

        MySpinnerAdapter() {
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
        }
    }

    int getOverallCost(){ return memoryCost; }
}
