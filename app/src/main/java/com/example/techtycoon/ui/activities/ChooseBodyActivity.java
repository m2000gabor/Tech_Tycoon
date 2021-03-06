package com.example.techtycoon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.FragmentDeviceCreator;
import com.example.techtycoon.MainActivity;
import com.example.techtycoon.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChooseBodyActivity extends AppCompatActivity {
    TextView[] valueTextViews;
    TextView[] costTextViews;
    List<SeekBar> seekbars;

    int[] bodyMaxLevels;
    int[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_body);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        int[] BODY_MAX_POINTS = new int[Device.NUMBER_OF_ATTRIBUTES];
        for(int i=0;i<Device.NUMBER_OF_ATTRIBUTES-2;i++){
            BODY_MAX_POINTS[i]= DevelopmentValidator.getMaxLevel(i+2);};
        */
        //get maxLevels
        bodyMaxLevels =getIntent().getIntArrayExtra(MainActivity.LEVELS);

        //find views
        valueTextViews=new TextView[]{
                findViewById(R.id.designCounterTextView),
                findViewById(R.id.materialCounterTextView),
                findViewById(R.id.colorsCounterTextView),
                findViewById(R.id.ipCounterTextView),
                findViewById(R.id.bezelsCounterTextView),
        };
        costTextViews=new TextView[]{
                findViewById(R.id.designCost),
                findViewById(R.id.materialCost),
                findViewById(R.id.colorsCost),
                findViewById(R.id.ipCost),
                findViewById(R.id.bezelsCost),
        };
        seekbars=new LinkedList<SeekBar>();
        seekbars.add(findViewById(R.id.designSeekBar));
        seekbars.add(findViewById(R.id.materialSeekBar));
        seekbars.add(findViewById(R.id.colorsSeekBar));
        seekbars.add(findViewById(R.id.ipSeekBar));
        seekbars.add(findViewById(R.id.bezelsSeekBar));

        result=new int[Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).size()];
        for (int i=0;i<Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).size();i++){
            Device.DeviceAttribute bodyAttribute= Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).get(i);
            valueTextViews[i].setText(Device.getStringFromAttributeLevel(bodyAttribute, 1));
            costTextViews[i].setText(String.format(Locale.getDefault(),"%d$",DeviceValidator.getCostOfAttribute(bodyAttribute,1)) );
            result[i]=1;
        }

        //set up an onclicklistener
        SeekBar.OnSeekBarChangeListener mListener=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress++;
                int i=seekbars.indexOf(seekBar);
                result[i]=progress;
                Device.DeviceAttribute bodyAttribute= Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).get(i);
                //valueTextViews[i].setText(String.format(Locale.getDefault(),"%d/%d points",BODY_MAX_POINTS[i],progress) );
                valueTextViews[i].setText(Device.getStringFromAttributeLevel(bodyAttribute, progress));
                costTextViews[i].setText(String.format(Locale.getDefault(),"%d$",DeviceValidator.getCostOfAttribute(bodyAttribute,progress)) );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        //set max and onclick listener
        for (int i=0;i<Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY).size();i++){
            seekbars.get(i).setMax(bodyMaxLevels[i]-1);
            seekbars.get(i).setOnSeekBarChangeListener(mListener);
        }

        FloatingActionButton fab = findViewById(R.id.saveBody);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cost=0;
                for (Device.DeviceAttribute a: Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY)){cost+=DeviceValidator.getCostOfAttribute(a,result[1]);}
                Intent intent=new Intent();
                intent.putExtra(FragmentDeviceCreator.CHOOSER_RESULTS_as_intArray,result);
                intent.putExtra("cost",cost);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

}
