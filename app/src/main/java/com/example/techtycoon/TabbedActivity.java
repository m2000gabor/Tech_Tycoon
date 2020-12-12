package com.example.techtycoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.techtycoon.Assistant.AssistantManager;
import com.example.techtycoon.dialogs.SortByDialog;
import com.example.techtycoon.simulator.Simulator;
import com.example.techtycoon.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

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
            editor.putFloat(getString(R.string.turn_counter),0);
            for(int i=0;i<Device.getAllAttribute().size();i++){
                editor.putFloat(Device.attributeToString(Device.getAllAttribute().get(i)),1);
            }

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
                float lastAvgPrice=sharedPref.getFloat(getString(R.string.simulator_lastAvgPrice)
                        ,Device.getMinimalDevice("",10,0).getPrice());
                double[] savedAvgs=new double[Device.getAllAttribute().size()];
                for(int i=0;i<Device.getAllAttribute().size();i++){
                    savedAvgs[i]=(sharedPref.getFloat(Device.attributeToString(Device.getAllAttribute().get(i)), 1));
                }
                float turn = sharedPref.getFloat(getString(R.string.turn_counter), 0);
                simulator=new Simulator(deviceViewModel.getAllDevicesList(),deviceViewModel.getAllCompaniesList(),lastAvgPrice,savedAvgs);
                Wrapped_DeviceAndCompanyList simulatorResults=simulator.simulate();
                oneMonthSimulated(simulatorResults);

                //save the avgs
                for(int i=0;i<Device.getAllAttribute().size();i++){
                    editor.putFloat(Device.attributeToString(Device.getAllAttribute().get(i)),(float)simulator.attrAverages[i]);
                }
                editor.putFloat(getString(R.string.simulator_lastAvgPrice), (float) simulator.lastAvgPrice);
                editor.putFloat(getString(R.string.turn_counter),++turn);
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
    public void selectedAttribute(Device.DeviceAttribute code) {
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