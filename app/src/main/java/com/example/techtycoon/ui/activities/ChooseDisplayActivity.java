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

public class ChooseDisplayActivity extends AppCompatActivity {
    TextView[] valueTextViews;
    TextView[] costTextViews;
    List<SeekBar> seekbars;

    int[] maxLevels;
    int[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get maxLevels
        maxLevels =getIntent().getIntArrayExtra(MainActivity.LEVELS);

        //find views
        valueTextViews=new TextView[]{
                findViewById(R.id.resolutionCounterTextView),
                findViewById(R.id.brightnessCounterTextView),
                findViewById(R.id.refreshRateCounterTextView),
                findViewById(R.id.displayTechnologyCounterTextView)
        };
        costTextViews=new TextView[]{
                findViewById(R.id.resolutionCost),
                findViewById(R.id.brightnessCost),
                findViewById(R.id.refreshRateCost),
                findViewById(R.id.displayTechnologyCost)
        };
        seekbars=new LinkedList<SeekBar>();
        seekbars.add(findViewById(R.id.resolutionSeekBar));
        seekbars.add(findViewById(R.id.brightnessSeekBar));
        seekbars.add(findViewById(R.id.refreshRateSeekBar));
        seekbars.add(findViewById(R.id.displayTechnologySeekBar));

        result=new int[Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY).size()];
        for (int i=0;i<Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY).size();i++){
            Device.DeviceAttribute attribute= Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY).get(i);
            valueTextViews[i].setText(Device.getStringFromAttributeLevel(attribute, 1));
            costTextViews[i].setText(String.format(Locale.getDefault(),"%d$",DeviceValidator.getCostOfAttribute(attribute,1)) );
            result[i]=1;
        }

        //set up an onclicklistener
        SeekBar.OnSeekBarChangeListener mListener=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress++;
                int i=seekbars.indexOf(seekBar);
                result[i]=progress;
                Device.DeviceAttribute bodyAttribute= Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY).get(i);
                valueTextViews[i].setText(Device.getStringFromAttributeLevel(bodyAttribute, progress));
                costTextViews[i].setText(String.format(Locale.getDefault(),"%d$",DeviceValidator.getCostOfAttribute(bodyAttribute,progress)) );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        //set max and onclick listener
        for (int i=0;i<Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY).size();i++){
            seekbars.get(i).setMax(maxLevels[i]-1);
            seekbars.get(i).setOnSeekBarChangeListener(mListener);
        }

        FloatingActionButton fab = findViewById(R.id.saveDisplay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cost=0;
                for (Device.DeviceAttribute a: Device.getAllAttribute_InBudget(Device.DeviceBudget.DISPLAY)){cost+=DeviceValidator.getCostOfAttribute(a,result[1]);}
                Intent intent=new Intent();
                intent.putExtra(FragmentDeviceCreator.CHOOSER_RESULTS_as_intArray,result);
                intent.putExtra("cost",cost);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

}

