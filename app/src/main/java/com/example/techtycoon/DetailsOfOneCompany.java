package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.techtycoon.DevelopmentValidator.calculateMarketingCost;

public class DetailsOfOneCompany extends AppCompatActivity {
    //random variables
    int id=-1;
    boolean isUpgrade=false;
    DeviceViewModel deviceViewModel;
    Company company;

    //views
    TextView moneyTextV;
    TextView levelsTextV;
    TextView marketingTextV;
    TextView slotsTextV;
    TextView logsTextV;
    Button oneRoundMarketingButton;
    Button buySlotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_one_company);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //find views
        TextView nevTextV =findViewById(R.id.name);
        moneyTextV =findViewById(R.id.money);
        TextView companyIDTextV =findViewById(R.id.companyId);
        levelsTextV=findViewById(R.id.levels);
        marketingTextV=findViewById(R.id.marketingTextV);
        slotsTextV=findViewById(R.id.slotsTextV);
        logsTextV=findViewById(R.id.companyLogs);
        oneRoundMarketingButton=findViewById(R.id.oneRoundMarketing);
        buySlotButton=findViewById(R.id.buySlot);

        //get data from previous activity
        Intent intent = getIntent();
        id=intent.getIntExtra("ID",-1);
        deviceViewModel=new DeviceViewModel(getApplication());
        company = deviceViewModel.getCompany_byID(id);

        //display data
        nevTextV.setText(company.name);
        companyIDTextV.setText(String.format(Locale.getDefault(),"ID: %d",id));
        oneRoundMarketingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(company.money>=calculateMarketingCost(company.marketing)){
                    company.money-=calculateMarketingCost(company.marketing);
                    company.marketing+=10;
                    updateCompany(true);
                }else{
                    Toast.makeText(v.getContext(),"Not enough money",Toast.LENGTH_SHORT).show();
                }
            }
        });
        buySlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(company.money>=DevelopmentValidator.nextSlotCost(company.maxSlots)){
                    company.money-=DevelopmentValidator.nextSlotCost(company.maxSlots);
                    company.maxSlots+=1;
                    updateCompany(true);
                }else{
                    Toast.makeText(v.getContext(),"Not enough money",Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.startDevelopmentActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.setClass(getBaseContext(),DevelopmentActivity.class);
                intent1.putExtra("ID",id);
                intent1.putExtra(MainActivity.LEVELS,company.getLevels_USE_THIS());
                intent1.putExtra(MainActivity.MAIN_MONETARILY_INFO,company.money);
                startActivityForResult(intent1,1);
            }
        });


       updateCompany(false);
    }


    public void startAssistantActivity(View view){
        Intent intent=new Intent()
                .setClass(this,AssistantActivity.class)
                .putExtra("ID",id);
        startActivity(intent);
    }

    public void delThisOne(View view) {
        deviceViewModel.delOneCompanyById(id);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK) {
            isUpgrade = data.getBooleanExtra("IS_UPDATE", false);
            if (isUpgrade) {
                company.setLevels_USE_THIS(data.getIntArrayExtra(MainActivity.LEVELS));
                company.money=data.getIntExtra(MainActivity.MAIN_MONETARILY_INFO,0);
                updateCompany(true);
            }
        }else {id=data.getIntExtra("ID",-1);}
    }

    void updateCompany(boolean updateTheDatabase){
        if(updateTheDatabase){deviceViewModel.updateCompanies(company);};
        moneyTextV.setText(String.format(Locale.getDefault(),"Money: %d",company.money));
        marketingTextV.setText(String.format(Locale.getDefault(),"Marketing: %d",company.marketing));
        slotsTextV.setText(String.format(Locale.getDefault(),"Slots: %d/%d",company.maxSlots,company.usedSlots));
        logsTextV.setText(company.logs);
        levelsTextV.setText(Converter.intArrayToString(company.getLevels_USE_THIS()));
        if(DevelopmentValidator.nextSlotCost(company.maxSlots)!=-1){
        buySlotButton.setText(String.format(Locale.getDefault(),
                "Buy a new slot for: %d$",DevelopmentValidator.nextSlotCost(company.maxSlots)));
        }else{buySlotButton.setClickable(false);}
        oneRoundMarketingButton.setText(String.format(Locale.getDefault(),
                "Launch marketing campaign for: %d$",calculateMarketingCost(company.marketing)));
    }



}

