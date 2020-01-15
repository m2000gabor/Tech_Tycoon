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
    int mRam=0;
    int mMemory=0;

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
                replyIntent.putExtra("costs",mMemory*MEMORY_PER_GB+mRam*RAM_PER_GB);
                setResult(RESULT_OK,replyIntent);
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView ramCounter=findViewById(R.id.ramCounterTextView);
        SeekBar ramSeekbar=findViewById(R.id.ramSeekBar);
        TextView ramCost=findViewById(R.id.ramCost);
        ramSeekbar.setMax(64);
        ramSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ramCounter.setText(String.valueOf(seekBar.getProgress())+" GB" );
            ramCost.setText(String.valueOf(seekBar.getProgress()*RAM_PER_GB)+"$" );
            mRam=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        TextView memoryCounter=findViewById(R.id.memoryCounterTextView);
        TextView memoryCost=findViewById(R.id.memoryCost);
        SeekBar memorySeekbar=findViewById(R.id.memorySeekBar);
        memorySeekbar.setMax(1024);
        memorySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                memoryCounter.setText(String.format(Locale.getDefault(),"%d GB",seekBar.getProgress()) );
                memoryCost.setText(String.format(Locale.getDefault(),"%.2f &",seekBar.getProgress()*MEMORY_PER_GB));
                mMemory=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

}
