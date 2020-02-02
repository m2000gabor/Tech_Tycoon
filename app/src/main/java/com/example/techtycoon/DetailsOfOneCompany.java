package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.techtycoon.Assistant.Assistant;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class DetailsOfOneCompany extends AppCompatActivity {
    private final static int[] SLOT_COSTS={0,100000,200000,300000,400000,500000,750000,1000000,1500000};

    //random variables
    int id;
    boolean isUpgrade=false;
    DeviceViewModel deviceViewModel;
    Company company;

    //views
    TextView moneyTextV;
    TextView levelsTextV;
    TextView marketingTextV;
    TextView slotsTextV;
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
                if(company.money>=10000){
                    company.money-=10000;
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
                if(company.money>=SLOT_COSTS[company.maxSlots]){
                    company.money-=SLOT_COSTS[company.maxSlots];
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
                intent1.putExtra(MainActivity.MAIN_MONETARIAL_INFO,company.money);
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
                company.money=data.getIntExtra(MainActivity.MAIN_MONETARIAL_INFO,0);
                updateCompany(true);
            }
        }else {id=data.getIntExtra("ID",-1);}
    }

    void updateCompany(boolean updateTheDatabase){
        if(updateTheDatabase){deviceViewModel.updateCompanies(company);};
        moneyTextV.setText(String.format(Locale.getDefault(),"Money: %d",company.money));
        marketingTextV.setText(String.format(Locale.getDefault(),"Marketing: %d",company.marketing));
        slotsTextV.setText(String.format(Locale.getDefault(),"Slots: %d/%d",company.maxSlots,company.usedSlots));
        levelsTextV.setText(Converter.intArrayToString(company.getLevels_USE_THIS()));
        buySlotButton.setText(String.format(Locale.getDefault(),
                "Buy a new slot for: %d$",SLOT_COSTS[company.maxSlots]));
        oneRoundMarketingButton.setText(String.format(Locale.getDefault(),
                "Launch marketing campaign for: %d$",calculateMarketingCost(company.marketing)));
    }

    public static int calculateMarketingCost(int actualMarketingLevel){
        return 10000+(actualMarketingLevel*100);
    }

}

