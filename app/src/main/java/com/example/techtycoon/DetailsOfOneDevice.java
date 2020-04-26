package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailsOfOneDevice extends AppCompatActivity {
    int id;
    int companyId;
    int profit;
    String name;
    Intent replyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_one_device);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //find views
        TextView nevTextV =findViewById(R.id.name);
        TextView profitTextV =findViewById(R.id.profit);
        TextView deviceIDTextV =findViewById(R.id.deviceID);
        TextView ownerIDTextV =findViewById(R.id.ownerID);
        TextView priceTextV =findViewById(R.id.price);
        TextView ramTextV =findViewById(R.id.ram);
        TextView memoryTextV =findViewById(R.id.memory);
        TextView costTextV =findViewById(R.id.cost);
        TextView bodyTextV =findViewById(R.id.bodyInfos);


        //get data from previous activity
        Intent intent = getIntent();
        name = intent.getStringExtra(MainActivity.NAME_FIELD);
        profit=intent.getIntExtra(MainActivity.MAIN_MONETARILY_INFO,0);
        companyId=intent.getIntExtra(MainActivity.DEVICE_COMPANY_ID,0);
        int price=intent.getIntExtra(MainActivity.DEVICE_PRICE,0);
        int cost=intent.getIntExtra(MainActivity.DEVICE_COST,0);
        int[][] devParams=Device.intArrayToMtx(intent.getIntArrayExtra(MainActivity.DEVICE_PARAMS));

        id=intent.getIntExtra("ID",-1);

        //display data
        nevTextV.setText("Name: "+name);
        priceTextV.setText(String.format(Locale.getDefault(),"Price: %d$",price));
        profitTextV.setText(String.format(Locale.getDefault(),"Profit: %d$",profit));
        deviceIDTextV.setText(String.format(Locale.getDefault(),"Device ID: %d",id));
        ownerIDTextV.setText(String.format(Locale.getDefault(),"Owner id: %d",companyId));
        ramTextV.setText(String.format(Locale.getDefault(),"RAM: %d GB",(int) Math.pow(2,devParams[0][0])));
        memoryTextV.setText(String.format(Locale.getDefault(),"Memory: %d GB",(int) Math.pow(2,devParams[0][1])));
        costTextV.setText(String.format(Locale.getDefault(),"Cost: %d$",cost));
        bodyTextV.setText(String.format(Locale.getDefault(),"Body: %s$",Converter.intArrayToString(devParams[1])));

        setResult(RESULT_OK,new Intent().putExtra(MainActivity.TASK_OF_RECYCLER_VIEW,MainActivity.DISPLAY_DEVICES_REQUEST_CODE));

        //init intent
        replyIntent=new Intent();
    }

    public void delThisOne(View view) {
        Intent replyIntent=new Intent();
        replyIntent.putExtra("IS_DELETE", true);
        replyIntent.putExtra("ID", id);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void editProfit(View v){
        TextView textView=findViewById(R.id.profit);
        if(textView.getVisibility()==View.VISIBLE) {
            EditText editText = findViewById(R.id.profitEditable);
            textView.setVisibility(View.GONE);
            editText.setText(String.valueOf(profit));
            editText.setVisibility(View.VISIBLE);
            ((Button) v).setText("Save");
        }else{
            EditText editText = findViewById(R.id.profitEditable);
            profit = Integer.parseInt(editText.getText().toString());
            editText.setVisibility(View.GONE);
            textView.setText(String.format(Locale.getDefault(),"Profit: %d$",profit));
            textView.setVisibility(View.VISIBLE);
            ((Button) v).setText("Edit");

            //make a replyIntent
            replyIntent.putExtra("isProfitChanged", true);
            replyIntent.putExtra("ID", id);
            replyIntent.putExtra("profit", profit);
            setResult(RESULT_OK, replyIntent);
        }
    }

    public void editName(View v){
        TextView textView=findViewById(R.id.name);
        if(textView.getVisibility()==View.VISIBLE) {
            EditText editText = findViewById(R.id.nameEditable);
            textView.setVisibility(View.GONE);
            editText.setText(name);
            editText.setVisibility(View.VISIBLE);
            ((Button) v).setText("Save");
        }else{
            EditText editText = findViewById(R.id.nameEditable);
            name = editText.getText().toString();
            editText.setVisibility(View.GONE);
            textView.setText(String.format(Locale.getDefault(),"Name: %s",name));
            textView.setVisibility(View.VISIBLE);
            ((Button) v).setText("Edit");

            //make a replyIntent
            replyIntent.putExtra("isNameChanged", true);
            replyIntent.putExtra("ID", id);
            replyIntent.putExtra("name", name);
            setResult(RESULT_OK, replyIntent);
        }
    }
}
