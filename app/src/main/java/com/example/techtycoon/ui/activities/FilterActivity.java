package com.example.techtycoon.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.example.techtycoon.Company;

import static com.example.techtycoon.Device.DeviceAttribute;

import com.example.techtycoon.Device;
import com.example.techtycoon.DeviceViewModel;
import com.example.techtycoon.FragmentAllDevices;
import com.example.techtycoon.R;
import com.example.techtycoon.ui.activities.expandableList.CustomExpandableListAdapter;
import com.example.techtycoon.ui.activities.expandableList.ExpandableListDataPump;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterActivity extends AppCompatActivity {
    private CustomExpandableListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        DeviceViewModel deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        List<Company> companies = deviceViewModel.getAllCompaniesList();
        List<Device> deviceList = deviceViewModel.getAllDevicesList();
        ChipGroup companyChipGroup = findViewById(R.id.companyChipGroup);

        for (Company c : companies) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, companyChipGroup, false);
            chip.setText(c.name);
            chip.setChecked(true);
            companyChipGroup.addView(chip);
        }

        //expandable list
        LinkedList<Pair<DeviceAttribute, List<String>>> listData = new LinkedList<>();
        LinkedList<DeviceAttribute> attributeList = new LinkedList<>();
        for (DeviceAttribute a : DeviceAttribute.values()) {
            if (a == DeviceAttribute.NAME || a == DeviceAttribute.PROFIT
                    || a == DeviceAttribute.DEVICE_ID || a == DeviceAttribute.PRICE ||
                    a == DeviceAttribute.INCOME || a == DeviceAttribute.OWNER_ID ||
                    a==DeviceAttribute.SOLD_PIECES) {
                continue;
            }
            Set<String> s = new HashSet<>();
            for (Device d : deviceList) {
                s.add(String.valueOf(d.getFieldByAttribute(a)));
            }
            ArrayList<String> sortedValues = new ArrayList<>(s);
            Collections.sort(sortedValues, (o1, o2) -> Integer.parseInt(o1) - Integer.parseInt(o2));
            listData.add(new Pair<>(a, sortedValues));
            attributeList.add(a);
        }
        attributeList.sort(Comparator.naturalOrder());

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        //expandable list setup
        expandableListAdapter = new CustomExpandableListAdapter(this, attributeList, listData);
        expandableListView.setAdapter(expandableListAdapter);


        //save
        /*
        findViewById(R.id.saveFilters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ArrayList<Integer> companyIds = new ArrayList<>();
                for (int i = 0; i < companyChipGroup.getChildCount(); i++) {
                    Chip c = (Chip) companyChipGroup.getChildAt(i);
                    if (c.isChecked()) {
                        companyIds.add(companies.get(i).companyId);
                    }
                }

                ArrayList<String> attributesAsString = new ArrayList<>();
                ArrayList<ArrayList<Integer>> valuesToAttribute = new ArrayList<>();

                for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                    ArrayList<Integer> values = new ArrayList<>();
                    for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                        LinearLayout ll = (LinearLayout) expandableListAdapter.getChildView(i, j, false, null, null);
                        CheckBox c = (CheckBox) ll.getChildAt(0);
                        if (c.isChecked()) {
                            values.add(Integer.parseInt(c.getText().toString()));
                        }
                    }
                    attributesAsString.add(listTitles.get(i));
                    valuesToAttribute.add(values);
                }
                intent.putExtra(FragmentAllDevices.COMPANY_ID, companyIds);
                intent.putExtra(FragmentAllDevices.ATTRIBUTES, attributeAsString);
                for(int i=0;i<attributeAsString.size();i++){
                    intent.putExtra(attributeAsString.get(i),valuesToAttribute.get(i));}

                setResult(RESULT_OK, intent);
                finish();
            }
        });

         */

        findViewById(R.id.saveFilters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ArrayList<Integer> companyIds = new ArrayList<>();
                for (int i = 0; i < companyChipGroup.getChildCount(); i++) {
                    Chip c = (Chip) companyChipGroup.getChildAt(i);
                    if (c.isChecked()) {
                        companyIds.add(companies.get(i).companyId);
                    }
                }

                ArrayList<String> attributesAsString = (ArrayList<String>) expandableListAdapter.results.stream()
                        .map(a ->a.first.toString()).collect(Collectors.toList());

                intent.putExtra(FragmentAllDevices.COMPANY_ID, companyIds);
                intent.putExtra(FragmentAllDevices.ATTRIBUTES, attributesAsString);
                for(int i=0;i<attributesAsString.size();i++){
                    intent.putExtra(attributesAsString.get(i),expandableListAdapter.results.get(i).second);}

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
