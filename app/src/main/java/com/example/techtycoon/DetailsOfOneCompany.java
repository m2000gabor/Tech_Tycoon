package com.example.techtycoon;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailsOfOneCompany extends AppCompatActivity {
    //costs
    private final int[] RAM_UPGRADE_COST={100000,200000,300000,500000,750000,1000000,3000000};
    private final int[] MEMORY_UPGRADE_COST={100000,200000,250000,400000,600000,800000,1000000,1300000,1500000,2500000};

    //random variables
    int id;
    int money;
    boolean isUpgrade=false;
    int ramLevel;
    int ramUpgradeCost;
    int memoryLevel;
    int memoryUpradeCost;

    //views
    TextView moneyTextV;
    Button ramUpgradeButton;
    Button memoryUpgradeButton;



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
        TextView ramLevelTextView=findViewById(R.id.ramLevel);
        TextView memoryLevelTextView=findViewById(R.id.memoryLevel);
        ramUpgradeButton=findViewById(R.id.ramUpgrade);
        memoryUpgradeButton=findViewById(R.id.memoryUpgrade);


        //get data from previous activity
        Intent intent = getIntent();
        String nev = intent.getStringExtra(MainActivity.NAME_FIELD);
        money=intent.getIntExtra(MainActivity.MAIN_MONETARIAL_INFO,0);

        ramLevel=intent.getIntExtra(MainActivity.RAM_LVL,1);
        ramUpgradeCost =RAM_UPGRADE_COST[ramLevel-1];
        memoryLevel=intent.getIntExtra(MainActivity.MEMORY_LVL,1);
        memoryUpradeCost =MEMORY_UPGRADE_COST[memoryLevel-1];

        id=intent.getIntExtra("ID",-1);

        //display data
        nevTextV.setText(nev);
        moneyTextV.setText(String.format(Locale.getDefault(),"Money: %d",money));
        companyIDTextV.setText(String.format(Locale.getDefault(),"ID: %d",id));


        //ram level row
        ramLevelTextView.setText(String.format(Locale.getDefault(),"lvl %d",ramLevel));
        ramUpgradeButton.setText(String.format(Locale.getDefault(),"Upgrade for %d$", ramUpgradeCost));
        ramUpgradeButton.setOnClickListener(v -> {
            ramLevel++;
            upgrageHappend(ramUpgradeButton,ramLevelTextView,ramLevel,RAM_UPGRADE_COST);
            ramUpgradeCost =RAM_UPGRADE_COST[ramLevel-1];
            ramUpgradeButton.setText(String.format(Locale.getDefault(),"Upgrade for %d$", ramUpgradeCost));
        });

        //memory level row
        memoryLevelTextView.setText(String.format(Locale.getDefault(),"lvl %d",memoryLevel));
        memoryUpgradeButton.setText(String.format(Locale.getDefault(),"upgrade for %d$", memoryUpradeCost));
        memoryUpgradeButton.setOnClickListener(v -> {
            memoryLevel++;
            upgrageHappend(memoryUpgradeButton,memoryLevelTextView,memoryLevel,MEMORY_UPGRADE_COST);
            memoryUpradeCost =MEMORY_UPGRADE_COST[memoryLevel-1];
            memoryUpgradeButton.setText(String.format(Locale.getDefault(),"upgrade for %d$", memoryUpradeCost));
        });

        //make the intent
        Intent resultIntent = new Intent().putExtra(MainActivity.TASK_OF_RECYCLER_VIEW,MainActivity.DISPLAY_COMPANIES_REQUEST_CODE);
        resultIntent.putExtra("IS_DELETE",false);
        resultIntent.putExtra("IS_UPDATE",false);

        findViewById(R.id.saveCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultIntent.putExtra("IS_UPDATE",isUpgrade);
                if(isUpgrade){
                    resultIntent.putExtra(MainActivity.RAM_LVL,ramLevel);
                    resultIntent.putExtra(MainActivity.MEMORY_LVL,memoryLevel);
                    resultIntent.putExtra(MainActivity.MAIN_MONETARIAL_INFO,money);
                    resultIntent.putExtra("ID", id);
                }
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });

        isUpgradesAvalaible();

        setResult(RESULT_OK,resultIntent);
    }


    public void delThisOne(View view) {
        Intent replyIntent=new Intent();
        replyIntent.putExtra("IS_DELETE", true);
        replyIntent.putExtra("ID", id);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    private void upgrageHappend(Button button,TextView levelTextViev,int level,int[] costArray){
        isUpgrade=true;
        money-=costArray[level-2];
        moneyTextV.setText(String.format(Locale.getDefault(),"Money: %d",money));
        isUpgradesAvalaible();
        levelTextViev.setText(String.format(Locale.getDefault(),"lvl %d",level));
    }

    private void isUpgradesAvalaible(){
        if(money<RAM_UPGRADE_COST[ramLevel-1] || ramLevel==RAM_UPGRADE_COST.length){ ramUpgradeButton.setClickable(false);
            ramUpgradeButton.setBackgroundColor(Color.parseColor("#E53935"));}

        if(money<MEMORY_UPGRADE_COST[memoryLevel-1] || memoryLevel==MEMORY_UPGRADE_COST.length){memoryUpgradeButton.setClickable(false);
            memoryUpgradeButton.setBackgroundColor(Color.parseColor("#E53935"));}
    }


}

