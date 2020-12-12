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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChooseMemoryActivity extends AppCompatActivity {
    private int[] currentLevels;

    //views
    TextView[] valueTextViews;
    TextView[] costTextViews;
    List<SeekBar> seekbars;

    /*
    int mRam=1;
    int mMemory=1;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_memory);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fabMemory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(FragmentDeviceCreator.CHOOSER_RESULTS_as_intArray, currentLevels);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //find views
        valueTextViews=new TextView[]{
                findViewById(R.id.ramCounterTextView),
                findViewById(R.id.memoryCounterTextView)
        };
        costTextViews=new TextView[]{
                findViewById(R.id.ramCost),
                findViewById(R.id.memoryCost)
        };

        seekbars=new ArrayList<SeekBar>();
        seekbars.add(findViewById(R.id.ramSeekBar));
        seekbars.add(findViewById(R.id.memorySeekBar));

        //get intent
        int[] storageMaxLevels = getIntent().getIntArrayExtra(MainActivity.LEVELS);

        currentLevels=new int[Device.getAllAttribute_InBudget(Device.DeviceBudget.STORAGE).size()];
        Arrays.fill(currentLevels,1);

        for (int i = 0;i<Device.getAllAttribute_InBudget(Device.DeviceBudget.STORAGE).size(); i++) {
            final Device.DeviceAttribute attribute=Device.getAllAttribute_InBudget(Device.DeviceBudget.STORAGE).get(i);
            seekbars.get(i).setMax(storageMaxLevels[i] - 1);
            valueTextViews[i].setText(Device.getStringFromAttributeLevel(attribute,1));
            costTextViews[i].setText(String.format(Locale.getDefault(), "%d$", DeviceValidator.getCostOfAttribute(Device.DeviceAttribute.STORAGE_RAM, 1)));

            seekbars.get(i).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int j=seekbars.indexOf(seekBar);
                    currentLevels[j] = progress + 1;
                    valueTextViews[j].setText(Device.getStringFromAttributeLevel(attribute,progress+1));
                    //valueTextViews[j].setText(String.format(Locale.getDefault(), "%d GB", (int) Math.round(Math.pow(2, mRam))));
                    costTextViews[j].setText(String.format(Locale.getDefault(), "%d$", DeviceValidator.getCostOfAttribute(attribute,progress+1 )));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        /*
        //memorySeekbar
        TextView memoryCounter=findViewById(R.id.memoryCounterTextView);
        TextView memoryCost=findViewById(R.id.memoryCost);
        SeekBar memorySeekbar=findViewById(R.id.memorySeekBar);
        memorySeekbar.setMax(memoryMaxLvl-1);
        //update ui
        memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",2) );
        memoryCost.setText(String.format(Locale.getDefault(),"%.2f$",DeviceValidator.getCostOfMemory(Device.DeviceAttribute.STORAGE_MEMORY,1)));

        memorySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMemory=progress+1;
                memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",(int) Math.round(Math.pow(2,mMemory))) );
                memoryCost.setText(String.format(Locale.getDefault(),"%.2f$",DeviceValidator.getCostOfMemory(Device.DeviceAttribute.STORAGE_MEMORY,mMemory)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });*/
    }

}
