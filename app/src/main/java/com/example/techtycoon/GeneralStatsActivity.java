package com.example.techtycoon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class GeneralStatsActivity extends AppCompatActivity {

    final int[] textViewIDs={
            R.id.lastAvgPrice,
            R.id.lastAvgMemory,
            R.id.lastAvgRam,
            R.id.lastAvgDesign,
            R.id.lastAvgMaterial,
            R.id.lastAvgColors,
            R.id.lastAvgIp,
            R.id.lastAvgBezels,
            R.id.numOfTurnsTextView
                            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_stats);

        ArrayList<String> values=getIntent().getStringArrayListExtra(FragmentAllCompanies.GENERAL_STATS);
        for (int i=0;i<textViewIDs.length;i++) {
            TextView tw = findViewById(textViewIDs[i]);
            tw.setText(String.format(Locale.getDefault(),"%s: %s",
                    getString(FragmentAllCompanies.sharedPrefKeyIds[i]), values.get(i)));
        }
    }


}
