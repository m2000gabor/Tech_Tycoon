package com.example.techtycoon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.techtycoon.ui.main.SectionsPagerAdapter;

public class TabbedActivity extends AppCompatActivity {
    DeviceViewModel deviceViewModel;
    Simulator simulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Get a new or existing ViewModel from the ViewModelProvider.
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);

        //get sharedPrefs
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        float lastAvgPrice = sharedPref.getFloat(getString(R.string.simulator_lastAvgPrice), 5);
        float lastAvgRam = sharedPref.getFloat(getString(R.string.simulator_lastAvgRam), 1);
        float lastAvgMemory = sharedPref.getFloat(getString(R.string.simulator_lastAvgMemory), 1);
        simulator=new Simulator(deviceViewModel,lastAvgPrice,lastAvgRam,lastAvgMemory);

        FloatingActionButton fab = findViewById(R.id.fabSimulate);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulator.simulate();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                float lastAvgPrice=(float) simulator.lastAvgPrice;
                editor.putFloat(getString(R.string.simulator_lastAvgPrice), lastAvgPrice);
                editor.putFloat(getString(R.string.simulator_lastAvgMemory),(float) simulator.lastAvgMemory);
                editor.putFloat(getString(R.string.simulator_lastAvgRam),(float) simulator.lastAvgRam);
                editor.apply();
                Toast.makeText(getApplicationContext(), "1 month simulated", Toast.LENGTH_SHORT).show();
            }
        });
    }
}