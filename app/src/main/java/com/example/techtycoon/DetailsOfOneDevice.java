package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class DetailsOfOneDevice extends AppCompatActivity {
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_one_device);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //find views
        TextView nevTextV =findViewById(R.id.name);
        TextView profitTextV =findViewById(R.id.profit);
        TextView deviceIDTextV =findViewById(R.id.deviceID);
        TextView ownerIDTextV =findViewById(R.id.ownerID);
        TextView priceTextV =findViewById(R.id.price);
        TextView ramTextV =findViewById(R.id.ram);
        TextView memoryTextV =findViewById(R.id.memory);
        TextView costTextV =findViewById(R.id.cost);


        //get data from previous activity
        Intent intent = getIntent();
        String nev = intent.getStringExtra(MainActivity.NAME_FIELD);
        int profit=intent.getIntExtra(MainActivity.MAIN_MONETARIAL_INFO,0);
        int companyId=intent.getIntExtra(MainActivity.DEVICE_COMPANY_ID,0);
        int price=intent.getIntExtra(MainActivity.DEVICE_PRICE,0);
        int ram=intent.getIntExtra(MainActivity.DEVICE_RAM,0);
        int mem=intent.getIntExtra(MainActivity.DEVICE_MEMORY,0);
        int cost=intent.getIntExtra(MainActivity.DEVICE_COST,0);

        id=intent.getIntExtra("ID",-1);

        //display data
        nevTextV.setText("Nev: "+nev);
        priceTextV.setText(String.format(Locale.getDefault(),"Price: %d$",price));
        profitTextV.setText(String.format(Locale.getDefault(),"Profit: %d$",profit));
        deviceIDTextV.setText(String.format(Locale.getDefault(),"Device ID: %d",id));
        ownerIDTextV.setText(String.format(Locale.getDefault(),"Owner id: %d",companyId));
        ramTextV.setText(String.format(Locale.getDefault(),"RAM: %d GB",ram));
        memoryTextV.setText(String.format(Locale.getDefault(),"Memory: %d GB",mem));
        costTextV.setText(String.format(Locale.getDefault(),"Cost: %d$",cost));

        setResult(RESULT_OK,new Intent().putExtra(MainActivity.TASK_OF_RECYCLER_VIEW,MainActivity.DISPLAY_DEVICES_REQUEST_CODE));

    }


    public void delThisOne(View view) {
        Intent replyIntent=new Intent();
        replyIntent.putExtra("IS_DELETE", true);
        replyIntent.putExtra("ID", id);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
