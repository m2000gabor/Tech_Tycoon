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

public class ChooseMemoryActivity extends AppCompatActivity {
    private static final double MEMORY_PER_GB = 0.17;
    private static final double RAM_PER_GB = 6.5;
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
                replyIntent.putExtra("costs",(int) Math.round(mMemory*MEMORY_PER_GB+mRam*RAM_PER_GB));
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView ramCounter=findViewById(R.id.ramCounterTextView);
        SeekBar ramSeekbar=findViewById(R.id.ramSeekBar);
        TextView ramCost=findViewById(R.id.ramCost);
        ramSeekbar.setMax(6);
        //update ui
        ramCounter.setText(String.format(Locale.getDefault(),"%d GB",1) );
        ramCost.setText(String.format(Locale.getDefault(),"%.2f$",1*RAM_PER_GB));

        ramSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRam=(int) Math.round(Math.pow(2,progress));
                ramCounter.setText(String.format(Locale.getDefault(),"%d GB",mRam ));
                ramCost.setText(String.format(Locale.getDefault(),"%.2f$",mRam*RAM_PER_GB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        TextView memoryCounter=findViewById(R.id.memoryCounterTextView);
        TextView memoryCost=findViewById(R.id.memoryCost);
        SeekBar memorySeekbar=findViewById(R.id.memorySeekBar);
        memorySeekbar.setMax(10);
        //update ui
        memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",1) );
        memoryCost.setText(String.format(Locale.getDefault(),"%.2f$",1*MEMORY_PER_GB));

        memorySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMemory=(int) Math.round(Math.pow(2,progress)) ;
                memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",mMemory) );
                memoryCost.setText(String.format(Locale.getDefault(),"%.2f$",mMemory*MEMORY_PER_GB));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

}
