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
        TextView nevTextV =findViewById(R.id.nev);
        TextView profitTextV =findViewById(R.id.profit);
        TextView deviceIDTextV =findViewById(R.id.deviceID);
        TextView ownerIDTextV =findViewById(R.id.ownerID);


        //get data from previous activity
        Intent intent = getIntent();
        String nev = intent.getStringExtra(MainActivity.NAME_FIELD);
        int profit=intent.getIntExtra(MainActivity.MAIN_MONETARIAL_INFO,0);
        int companyId=intent.getIntExtra(MainActivity.DEVICE_COMPANY_ID,0);

        id=intent.getIntExtra("ID",-1);

        //display data
        nevTextV.setText(nev);
        profitTextV.setText(String.format(Locale.getDefault(),"Profit: %d",profit));
        deviceIDTextV.setText(String.format(Locale.getDefault(),"Device ID: %d",id));
        ownerIDTextV.setText(String.format(Locale.getDefault(),"Owner id: %d",companyId));

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
