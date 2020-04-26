package com.example.techtycoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.techtycoon.Assistant.AssistantManager;
import com.example.techtycoon.dialogs.SortByDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.example.techtycoon.ui.main.SectionsPagerAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabbedActivity extends AppCompatActivity  implements ChooseADeviceDialogFragment.NoticeDialogListener, SortByDialog.SortByDialogListener {
    public static HashMap<String,Integer> NAME_newestPartOfTheSeries=new HashMap<>();

    DeviceViewModel deviceViewModel;
    Simulator simulator;
    AssistantManager assistantManager;
    boolean assistantTurn;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1,true);

        // Get a new or existing ViewModel from the ViewModelProvider.
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);

        assistantManager=new AssistantManager();
        //get sharedPref
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(getIntent().getBooleanExtra("NEED_RESET",false)){
            assistantTurn=false;
            editor.putFloat(getString(R.string.simulator_lastAvgPrice), 100);
            editor.putFloat(getString(R.string.simulator_lastAvgMemory),1);
            editor.putFloat(getString(R.string.simulator_lastAvgRam),1);
            editor.putFloat(getString(R.string.simulator_lastAvgDesign),1);
            editor.putFloat(getString(R.string.simulator_lastAvgMaterial),1);
            editor.putFloat(getString(R.string.simulator_lastAvgColors),1);
            editor.putFloat(getString(R.string.simulator_lastAvgIp),1);
            editor.putFloat(getString(R.string.simulator_lastAvgBezels),1);

            Set<String> nameTableKeysSet=NAME_newestPartOfTheSeries.keySet();
            editor.putStringSet("nameTableKeys",nameTableKeysSet);
            for (String key :nameTableKeysSet) {editor.putInt(key,1);}
            editor.apply();

            //Log.d("SharedPrefDeletion", "Need deletion");
        }else{
            //Log.d("SharedPrefDeletion", "Dont need deletion");
            assistantTurn=getSharedPreferences("isAssistantTurn",MODE_PRIVATE).getBoolean("isAssistantTurn",false);
        }

        FloatingActionButton fab = findViewById(R.id.fabSimulate);
        if(assistantTurn){fab.setImageResource(R.drawable.ic_account_circle_white_24dp);}
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!assistantTurn){
                //get sharedPrefs
                float lastAvgPrice = sharedPref.getFloat(getString(R.string.simulator_lastAvgPrice), 5);
                float lastAvgRam = sharedPref.getFloat(getString(R.string.simulator_lastAvgRam), 1);
                float lastAvgMemory = sharedPref.getFloat(getString(R.string.simulator_lastAvgMemory), 1);
                float lastAvgDesign = sharedPref.getFloat(getString(R.string.simulator_lastAvgDesign), 1);
                float lastAvgMaterial = sharedPref.getFloat(getString(R.string.simulator_lastAvgMaterial), 1);
                float lastAvgColors = sharedPref.getFloat(getString(R.string.simulator_lastAvgColors), 1);
                float lastAvgIp = sharedPref.getFloat(getString(R.string.simulator_lastAvgIp), 1);
                float lastAvgBezels = sharedPref.getFloat(getString(R.string.simulator_lastAvgBezels), 1);
                double[] arr={(double) lastAvgRam,(double) lastAvgMemory,(double) lastAvgDesign,(double)lastAvgMaterial,(double)lastAvgColors,(double) lastAvgIp,(double)lastAvgBezels};

                simulator=new Simulator(deviceViewModel.getAllDevicesList(),deviceViewModel.getAllCompaniesList(),lastAvgPrice,arr);
                Wrapped_DeviceAndCompanyList simulatorResults=simulator.simulate();
                oneMonthSimulated(simulatorResults);

                editor.putFloat(getString(R.string.simulator_lastAvgPrice), (float) simulator.lastAvgPrice);
                editor.putFloat(getString(R.string.simulator_lastAvgMemory),(float) simulator.attrAverages[0]);
                editor.putFloat(getString(R.string.simulator_lastAvgRam),(float) simulator.attrAverages[1]);
                editor.putFloat(getString(R.string.simulator_lastAvgDesign),(float) simulator.attrAverages[2]);
                editor.putFloat(getString(R.string.simulator_lastAvgMaterial),(float) simulator.attrAverages[3]);
                editor.putFloat(getString(R.string.simulator_lastAvgColors),(float) simulator.attrAverages[4]);
                editor.putFloat(getString(R.string.simulator_lastAvgIp),(float) simulator.attrAverages[5]);
                editor.putFloat(getString(R.string.simulator_lastAvgBezels),(float) simulator.attrAverages[6]);
                editor.apply();
                fab.setImageResource(R.drawable.ic_account_circle_white_24dp);
                //Toast.makeText(getApplicationContext(), "1 month simulated", Toast.LENGTH_SHORT).show();

            }else{
                    //get nameTable for bots naming convention
                    Set<String> keys=sharedPref.getStringSet("nameTableKeys",new HashSet<String>());
                    for (String key : keys) {NAME_newestPartOfTheSeries.put(key,sharedPref.getInt(key, 1));}

                    Wrapped_DeviceAndCompanyList afterAssistants=assistantManager.trigger(deviceViewModel.getAllCompaniesList(),deviceViewModel.getAllDevicesList());
                    deviceViewModel.assistantToDatabase(afterAssistants);
                    //Toast.makeText(getApplicationContext(), "Assistants finished the work", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean("isAssistantTurn",assistantTurn);
        editor.apply();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getBooleanExtra("IS_DELETE",false)) {
            deviceViewModel.delOneDeviceById(data.getIntExtra("ID",-1));
            Toast.makeText(getBaseContext(), "Successful deletion", Toast.LENGTH_LONG).show();
        }else {
            if(resultCode == RESULT_OK && data.getBooleanExtra("isProfitChanged",false)){
                int id=data.getIntExtra("ID",-1);
                int profit=data.getIntExtra("profit",-1);
                Device device=deviceViewModel.getDevice_byID(id);
                device.profit=profit;
                deviceViewModel.updateDevices(device);
                Toast.makeText(getBaseContext(), "Profit is updated", Toast.LENGTH_SHORT).show();
            }
            if(resultCode == RESULT_OK && data.getBooleanExtra("isNameChanged",false)){
                int id=data.getIntExtra("ID",-1);
                String name=data.getStringExtra("name");
                Device device=deviceViewModel.getDevice_byID(id);
                device.name=name;
                deviceViewModel.updateDevices(device);
                Toast.makeText(getBaseContext(), "Name is updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void oneMonthSimulated(Wrapped_DeviceAndCompanyList results){
        results.companies.sort((a,b)->(b.lastProfit-a.lastProfit));
        for(int i=0;i<results.companies.size();i++){
            results.companies.get(i).marketPosition=i+1;
        }
        deviceViewModel.updateCompanies(results.companies.toArray(new Company[0]));
        deviceViewModel.updateDevices(results.devices.toArray(new Device[0]));
    }

    //called from the chooseDeviceForCloning dialogFragment
    @Override
    public void selectedDeviceID(int id) {
        FragmentDeviceCreator fragmentDeviceCreator= (FragmentDeviceCreator)
                getSupportFragmentManager().findFragmentById(R.id.fragment_device_creator);

        if (fragmentDeviceCreator != null) {
            fragmentDeviceCreator.loadInADevice(id);
        }else{
            fragmentDeviceCreator= (FragmentDeviceCreator)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_my_company);
            fragmentDeviceCreator.loadInADevice(id);
        }
    }

    @Override
    public void selectedAttributeId(Device.DeviceAttribute code) {
        List<Fragment> f=getSupportFragmentManager().getFragments();
        boolean found=false;
        for (int i=0;i<f.size() && !found;i++) {
            try {
                FragmentAllDevices fragmentAllDevices = (FragmentAllDevices) f.get(i);
                fragmentAllDevices.sortBy(code);
                found=true;
            }catch (ClassCastException ignored){}
        }
    }
}