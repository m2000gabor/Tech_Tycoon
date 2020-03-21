package com.example.techtycoon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.techtycoon.Assistant.AssistantManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.example.techtycoon.ui.main.SectionsPagerAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TabbedActivity extends AppCompatActivity {
    public static HashMap<String,Integer> NAME_newestPartOfTheSeries=new HashMap<>();

    DeviceViewModel deviceViewModel;
    Simulator simulator;
    AssistantManager assistantManager;
    boolean assistantTurn=false;

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

        assistantManager=new AssistantManager();


        FloatingActionButton fab = findViewById(R.id.fabSimulate);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!assistantTurn){
                //get sharedPrefs
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                float lastAvgPrice = sharedPref.getFloat(getString(R.string.simulator_lastAvgPrice), 5);
                float lastAvgRam = sharedPref.getFloat(getString(R.string.simulator_lastAvgRam), 1);
                float lastAvgMemory = sharedPref.getFloat(getString(R.string.simulator_lastAvgMemory), 1);
                float lastAvgDesign = sharedPref.getFloat(getString(R.string.simulator_lastAvgDesign), 1);
                float lastAvgMaterial = sharedPref.getFloat(getString(R.string.simulator_lastAvgMaterial), 1);
                float lastAvgColors = sharedPref.getFloat(getString(R.string.simulator_lastAvgColors), 1);
                float lastAvgIp = sharedPref.getFloat(getString(R.string.simulator_lastAvgIp), 1);
                float lastAvgBezels = sharedPref.getFloat(getString(R.string.simulator_lastAvgBezels), 1);
                double[] arr={(double) lastAvgDesign,(double)lastAvgMaterial,(double)lastAvgColors,(double) lastAvgIp,(double)lastAvgBezels};

                simulator=new Simulator(deviceViewModel,lastAvgPrice,lastAvgRam,lastAvgMemory,arr);
                Wrapped_DeviceAndCompanyList simulatorResults=simulator.simulate();
                //oneMonthSimulated(simulatorResults);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat(getString(R.string.simulator_lastAvgPrice), (float) simulator.lastAvgPrice);
                editor.putFloat(getString(R.string.simulator_lastAvgMemory),(float) simulator.lastAvgMemory);
                editor.putFloat(getString(R.string.simulator_lastAvgRam),(float) simulator.lastAvgRam);
                editor.putFloat(getString(R.string.simulator_lastAvgDesign),(float) simulator.averages[0]);
                editor.putFloat(getString(R.string.simulator_lastAvgMaterial),(float) simulator.averages[1]);
                editor.putFloat(getString(R.string.simulator_lastAvgColors),(float) simulator.averages[2]);
                editor.putFloat(getString(R.string.simulator_lastAvgIp),(float) simulator.averages[3]);
                editor.putFloat(getString(R.string.simulator_lastAvgBezels),(float) simulator.averages[4]);
                editor.apply();
                fab.setImageResource(R.drawable.ic_account_circle_white_24dp);
                Toast.makeText(getApplicationContext(), "1 month simulated", Toast.LENGTH_SHORT).show();

            }else{
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    //get nameTable for bots naming convention
                    Set<String> keys=sharedPref.getStringSet("nameTableKeys",new HashSet<String>());
                    for (String key : keys) {NAME_newestPartOfTheSeries.put(key,sharedPref.getInt(key, -1));}

                    Wrapped_DeviceAndCompanyList afterAssistants=assistantManager.trigger(deviceViewModel.getAllCompaniesList(),deviceViewModel.getAllDevicesList());
                    deviceViewModel.assistantToDatabase(afterAssistants);
                    Toast.makeText(getApplicationContext(), "Assistants finished the work", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    Set<String> nameTableKeysSet=NAME_newestPartOfTheSeries.keySet();
                    editor.putStringSet("nameTableKeys",nameTableKeysSet);
                    for (String key :nameTableKeysSet) {editor.putInt(key,NAME_newestPartOfTheSeries.get(key));}
                    editor.apply();
                }
            assistantTurn=!assistantTurn;
        }
        });
    }
    /*
    void oneMonthSimulated(Wrapped_DeviceAndCompanyList simulatorResults){
        Wrapped_DeviceAndCompanyList afterAssistants=assistantManager.trigger(simulatorResults.companies,simulatorResults.devices);
        deviceViewModel.assistantToDatabase(afterAssistants);
    }*/
}