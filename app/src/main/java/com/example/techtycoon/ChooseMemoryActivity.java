package com.example.techtycoon;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import static com.example.techtycoon.Simulator.log2;

//TODO dynamic costs

public class ChooseMemoryActivity extends AppCompatActivity {
    int mRam=1;
    int mMemory=1;

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
                Intent replyIntent=new Intent();
                replyIntent.putExtra("amountOfRam",mRam);
                replyIntent.putExtra("amountOfMemory",mMemory);
                replyIntent.putExtra("costs",
                        (int) Math.round(DeviceValidator.getCostOfMemory(0,mMemory)+
                        DeviceValidator.getCostOfMemory(1,mRam)));
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView ramCounter=findViewById(R.id.ramCounterTextView);
        SeekBar ramSeekbar=findViewById(R.id.ramSeekBar);
        TextView ramCost=findViewById(R.id.ramCost);

        //get intent
        int ramLvl=getIntent().getIntExtra(MainActivity.RAM_LVL,1);
        int memoryLvl=getIntent().getIntExtra(MainActivity.MEMORY_LVL,1);

        //ramSeekbar
        ramSeekbar.setMax(ramLvl-1);
        //update ui
        ramCounter.setText(String.format(Locale.getDefault(),"%d GB",2) );
        ramCost.setText(String.format(Locale.getDefault(),"%.2f$",DeviceValidator.getCostOfMemory(1,1)));

        ramSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRam=progress+1;
                ramCounter.setText(String.format(Locale.getDefault(),"%d GB",(int) Math.round(Math.pow(2,mRam)) ));
                ramCost.setText(String.format(Locale.getDefault(),"%.2f$",DeviceValidator.getCostOfMemory(1,mRam)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //memorySeekbar
        TextView memoryCounter=findViewById(R.id.memoryCounterTextView);
        TextView memoryCost=findViewById(R.id.memoryCost);
        SeekBar memorySeekbar=findViewById(R.id.memorySeekBar);
        memorySeekbar.setMax(memoryLvl-1);
        //update ui
        memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",2) );
        memoryCost.setText(String.format(Locale.getDefault(),"%.2f$",DeviceValidator.getCostOfMemory(0,1)));

        memorySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMemory=progress+1;
                memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",(int) Math.round(Math.pow(2,mMemory))) );
                memoryCost.setText(String.format(Locale.getDefault(),"%.2f$",DeviceValidator.getCostOfMemory(0,mMemory)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

}
