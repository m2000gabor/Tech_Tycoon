package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class AddNewCompany extends AppCompatActivity {


    private EditText companyNameField;
    private EditText moneyNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_company);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //find fields
        companyNameField = findViewById(R.id.companyNameInputField);
        moneyNameField = findViewById(R.id.moneyInputField);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void addCompany(View view){

        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(companyNameField.getText()) || TextUtils.isEmpty(moneyNameField.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            String companyName = companyNameField.getText().toString();
            int money = Integer.parseInt(moneyNameField.getText().toString());
            replyIntent.putExtra(MainActivity.NAME_FIELD, companyName);
            replyIntent.putExtra(MainActivity.MAIN_MONETARILY_INFO,money);
            setResult(RESULT_OK, replyIntent);
        }
        finish();
    }
}

