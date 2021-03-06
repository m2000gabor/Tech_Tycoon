package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.techtycoon.Device.NUMBER_OF_ATTRIBUTES;


public class DevelopmentActivity extends AppCompatActivity {

    boolean isUpgrade=false;
    int money;
    int[] levels;
    TextView moneyTextView;
    TextView[] actualLevelsTextViews;
    TextView[] costsTextViews;
    List<ImageButton> imageButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_development);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initalize arrays and the list
        moneyTextView=findViewById(R.id.money_Development);
        actualLevelsTextViews=new TextView[NUMBER_OF_ATTRIBUTES];
        costsTextViews=new TextView[NUMBER_OF_ATTRIBUTES];
        imageButtons=new LinkedList<>();

        //find views
        actualLevelsTextViews[0]= findViewById(R.id.ramLevel2);
        actualLevelsTextViews[1]= findViewById(R.id.memoryLevel2);
        actualLevelsTextViews[2]= findViewById(R.id.DesignLevel2);
        actualLevelsTextViews[3]= findViewById(R.id.MaterialLevel);
        actualLevelsTextViews[4]= findViewById(R.id.ColorsLevel);
        actualLevelsTextViews[5]= findViewById(R.id.IpLevel);
        actualLevelsTextViews[6]= findViewById(R.id.BezelsLevel);
        actualLevelsTextViews[7]= findViewById(R.id.resolutionLevel);
        actualLevelsTextViews[8]= findViewById(R.id.brightnessLevel);
        actualLevelsTextViews[9]= findViewById(R.id.refreshRateLevel);
        actualLevelsTextViews[10]= findViewById(R.id.displayTechnologyLevel);

        costsTextViews[0]= findViewById(R.id.ramUpgradeCost);
        costsTextViews[1]= findViewById(R.id.memoryUpgradeCost);
        costsTextViews[2]= findViewById(R.id.DesignUpgradeCost);
        costsTextViews[3]= findViewById(R.id.MaterialUpgradeCost);
        costsTextViews[4]= findViewById(R.id.ColorsUpgradeCost);
        costsTextViews[5]= findViewById(R.id.IpUpgradeCost);
        costsTextViews[6]= findViewById(R.id.BezelsUpgradeCost);
        costsTextViews[7]= findViewById(R.id.resolutionCost);
        costsTextViews[8]= findViewById(R.id.brightnessCost);
        costsTextViews[9]= findViewById(R.id.refreshRateCost);
        costsTextViews[10]= findViewById(R.id.displayTechnologyCost);


        imageButtons.add(findViewById(R.id.ImageButton_RamUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_MemoryUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_DesignUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_MaterialUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_ColorsUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_IpUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_BezelsUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_ResolutionUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_brightnessUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_refreshRateUpgrade));
        imageButtons.add(findViewById(R.id.ImageButton_displayTechnologyUpgrade));

        //get intent
        Intent data=getIntent();
        money=data.getIntExtra(MainActivity.MAIN_MONETARILY_INFO,0);
        levels=data.getIntArrayExtra(MainActivity.LEVELS);

        //make replyIntent with fab
        FloatingActionButton fab = findViewById(R.id.saveUpgrades);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("IS_UPDATE",isUpgrade);
                if(isUpgrade){
                    resultIntent.putExtra(MainActivity.LEVELS,levels);
                    resultIntent.putExtra(MainActivity.MAIN_MONETARILY_INFO,money);
                }
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int id =getIntent().getIntExtra("ID",0);
        setResult(RESULT_FIRST_USER,new Intent().putExtra("ID",id));

        //set text and available items
        refresh();
    }


    void refresh(){
        moneyTextView.setText(String.format(Locale.getDefault(),"Money: %d$",money));
        for(int i = 0; i< NUMBER_OF_ATTRIBUTES; i++){
            Device.DeviceAttribute attribute= Device.getAllAttribute().get(i);
            costsTextViews[i].setText(String.format(Locale.getDefault(),"%d$", DevelopmentValidator.getOneDevelopmentCost(attribute,levels[i])));
            actualLevelsTextViews[i].setText(String.format(Locale.getDefault(),"lvl %d", levels[i]));

            //if maximum level is reached
            if (-1== DevelopmentValidator.getOneDevelopmentCost(attribute,levels[i])){
                imageButtons.get(i).setVisibility(View.INVISIBLE);
                costsTextViews[i].setText("Reached max level");
                continue;
            }
            if(money>= DevelopmentValidator.getOneDevelopmentCost(attribute,levels[i])){
                imageButtons.get(i).setImageDrawable(getDrawable(R.drawable.ic_upgrade_green_24dp));
                imageButtons.get(i).setClickable(true);
                imageButtons.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i=imageButtons.indexOf((ImageButton) v);
                        money-= DevelopmentValidator.getOneDevelopmentCost(attribute,levels[i]);
                        levels[i]++;
                        isUpgrade=true;
                        refresh();
                    }
                });
            }else {
                imageButtons.get(i).setImageDrawable(getDrawable(R.drawable.ic_cancel_red_24dp));
                imageButtons.get(i).setClickable(false);
            }
        }
    }


}
