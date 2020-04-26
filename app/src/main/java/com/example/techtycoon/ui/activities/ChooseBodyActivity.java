package com.example.techtycoon.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.techtycoon.DevelopmentValidator;
import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceValidator;
import com.example.techtycoon.FragmentDeviceCreator;
import com.example.techtycoon.MainActivity;
import com.example.techtycoon.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ChooseBodyActivity extends AppCompatActivity {
    private int[] BODY_MAX_POINTS;
    TextView[] valueTextViews;
    TextView[] costTextViews;
    List<SeekBar> seekbars;

    int[] levels;
    int[] result;
    int cost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_body);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BODY_MAX_POINTS=new int[Device.NUMBER_OF_ATTRIBUTES];
        for(int i=0;i<Device.NUMBER_OF_ATTRIBUTES-2;i++){
            BODY_MAX_POINTS[i]= DevelopmentValidator.getMaxLevel(i+2);};

        //get int
        levels=getIntent().getIntArrayExtra(MainActivity.LEVELS);

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

        result=new int[Device.CHILDREN_OF_BUDGETS[1]];
        for (int i=0;i<Device.CHILDREN_OF_BUDGETS[1];i++){
            Device.DeviceAttribute bodyAttribute=Device.getBodyAttributes().get(i);
            valueTextViews[i].setText(String.format(Locale.getDefault(),"%d/%d points",BODY_MAX_POINTS[i],1) );
            costTextViews[i].setText(String.format(Locale.getDefault(),"%d$", DeviceValidator.getCostOfBody(bodyAttribute,1)) );
            result[i]=1;
        }

        //set up an onclicklistener
        SeekBar.OnSeekBarChangeListener mListener=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress++;
                int i=seekbars.indexOf(seekBar);
                result[i]=progress;
                Device.DeviceAttribute bodyAttribute=Device.getBodyAttributes().get(i);
                valueTextViews[i].setText(String.format(Locale.getDefault(),"%d/%d points",BODY_MAX_POINTS[i],progress) );
                costTextViews[i].setText(String.format(Locale.getDefault(),"%d$",DeviceValidator.getCostOfBody(bodyAttribute,progress)) );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        //set max and onclick listener
        for (int i=0;i<Device.CHILDREN_OF_BUDGETS[1];i++){
            seekbars.get(i).setMax(levels[i+2]-1);
            seekbars.get(i).setOnSeekBarChangeListener(mListener);
        }

        FloatingActionButton fab = findViewById(R.id.saveBody);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Device.DeviceAttribute a: Device.getBodyAttributes()){cost+=DeviceValidator.getCostOfBody(a,result[1]);}
                Intent intent=new Intent();
                intent.putExtra(FragmentDeviceCreator.BODY_RESULTS,result);
                intent.putExtra("cost",cost);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }

}
