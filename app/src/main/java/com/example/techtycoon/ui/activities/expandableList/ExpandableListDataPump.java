package com.example.techtycoon.ui.activities.expandableList;


import com.example.techtycoon.Device;

import static com.example.techtycoon.Device.DeviceAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpandableListDataPump {
    public static HashMap<DeviceAttribute, List<String>> getData(List<Device> deviceList) {
        HashMap<DeviceAttribute, List<String>> expandableListDetail = new HashMap<>();
        List<DeviceAttribute> attributeList=Arrays.asList(DeviceAttribute.values());
        attributeList.sort(Comparator.naturalOrder());
        for (DeviceAttribute a : attributeList) {
            if(a==DeviceAttribute.NAME || a==DeviceAttribute.PROFIT
                    ||a==DeviceAttribute.DEVICE_ID||a==DeviceAttribute.PRICE||
                    a==DeviceAttribute.INCOME || a==DeviceAttribute.OWNER_ID){continue;}
            Set<String> s = new HashSet<>();
            for (Device d : deviceList) {
                s.add(String.valueOf(d.getFieldByAttribute(a)));
            }
            ArrayList<String> sortedValues=new ArrayList<>(s);
            Collections.sort(sortedValues, (o1, o2) -> Integer.parseInt(o1)-Integer.parseInt(o2));
            expandableListDetail.put(a,sortedValues);
        }

        return expandableListDetail;
    }
}

