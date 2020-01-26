package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailsOfOneCompany extends AppCompatActivity {
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_one_company);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //find views
        TextView nevTextV =findViewById(R.id.name);
        TextView moneyTextV =findViewById(R.id.money);
        TextView companyIDTextV =findViewById(R.id.companyId);


        //get data from previous activity
        Intent intent = getIntent();
        String nev = intent.getStringExtra(MainActivity.NAME_FIELD);
        int profit=intent.getIntExtra(MainActivity.MAIN_MONETARIAL_INFO,0);

        id=intent.getIntExtra("ID",-1);

        //display data
        nevTextV.setText(nev);
        moneyTextV.setText(String.format(Locale.getDefault(),"Money: %d",profit));
        companyIDTextV.setText(String.format(Locale.getDefault(),"ID: %d",id));

        Intent resultIntent = new Intent().putExtra(MainActivity.TASK_OF_RECYCLER_VIEW,MainActivity.DISPLAY_COMPANIES_REQUEST_CODE);
        resultIntent.putExtra("IS_DELETE",false);
        setResult(RESULT_OK,resultIntent);
    }


    public void delThisOne(View view) {
        Intent replyIntent=new Intent();
        replyIntent.putExtra("IS_DELETE", true);
        replyIntent.putExtra("ID", id);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}

