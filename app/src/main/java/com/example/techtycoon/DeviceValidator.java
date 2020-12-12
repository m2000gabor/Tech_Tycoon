package com.example.techtycoon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.example.techtycoon.Device.DeviceAttribute;

//primary source for assistant, should be used in fragments too
public class DeviceValidator {
    //TODO real dynamic costs
    /*
    private static final double[] MEMORY_COSTS={4,2};
    private static final int[] BODY_COSTS={5,3,1,2,3};
    private static final int[] DISPLAY_COSTS={4,3,5,6};*/
    private static final Map<Device.DeviceAttribute,Integer> costsMap;
    static {
        Map<Device.DeviceAttribute,Integer> aMap =new HashMap<>();
        aMap.put(DeviceAttribute.STORAGE_RAM,4);
        aMap.put(DeviceAttribute.STORAGE_MEMORY,2);
        aMap.put(DeviceAttribute.BODY_DESIGN,5);
        aMap.put(DeviceAttribute.BODY_MATERIAL,3);
        aMap.put(DeviceAttribute.BODY_COLOR,1);
        aMap.put(DeviceAttribute.BODY_IP,2);
        aMap.put(DeviceAttribute.BODY_BEZEL,3);
        aMap.put(DeviceAttribute.DISPLAY_RESOLUTION,4);
        aMap.put(DeviceAttribute.DISPLAY_BRIGHTNESS,3);
        aMap.put(DeviceAttribute.DISPLAY_REFRESH_RATE,5);
        aMap.put(DeviceAttribute.DISPLAY_TECHNOLOGY,6);
        costsMap = Collections.unmodifiableMap(aMap);
    }

    //todo implement a validation process
    public static boolean validate(Company myCompany,Device newDev){return true;}
    public DeviceValidator(){}

    //calculate the cost of a param or a whole budget
    //@param budgets
    /*
    private static double getCostOfBudgets(int budget, Device.DeviceAttribute attribute, int value, Device device){
        double cost=0;
        switch (budget){
            //memory
            case 0:
                if(attribute==null){
                    for(Device.DeviceAttribute a : Device.getAllAttribute_InBudget(Device.DeviceBudget.STORAGE)){
                        cost+=getCostOfMemory(a,device.getFieldByAttribute(a));}
                }else{ cost=getCostOfMemory(attribute,value);}break;
            //body
            case 1: if(attribute==null){
                for(Device.DeviceAttribute a : Device.getAllAttribute_InBudget(Device.DeviceBudget.BODY)){
                    cost+=(double) getCostOfBody(a,device.getFieldByAttribute(a)); }
            }else{ cost=(double) getCostOfBody(attribute,value);}break;
        }
        return cost;
    }*/

    public static int getCostOfBudget(Device.DeviceBudget budget,Device device){
        int cost=0;
        for(Device.DeviceAttribute a:Device.getAllAttribute_InBudget(budget)){
            cost+=getCostOfAttribute(a,device.getFieldByAttribute(a));
        }
        return cost;
    }

    public static int getCostOfAttribute(DeviceAttribute a,int level){
        switch (a){
            default:return (int) (costsMap.get(a)*1.5*level);
            case STORAGE_RAM:
            case STORAGE_MEMORY:
                return (int) (level * costsMap.get(a)*Math.pow(1.15,level));
        }
    }


/*
    public static double getCostOfMemory(Device.DeviceAttribute a, int value){
        if(a== Device.DeviceAttribute.STORAGE_RAM){
            return value * MEMORY_COSTS[0]*Math.pow(1.15,value);
        }else if(a== STORAGE_MEMORY){
            return value * MEMORY_COSTS[1]*Math.pow(1.15,value);
        }else{throw new IllegalArgumentException("Invalid attribute");}
    }

    public static int getCostOfBody(Device.DeviceAttribute attribute, int value){
        double cost=0;
        switch(attribute) {
            //design,material,color,ip,bezel
            case BODY_DESIGN:cost =BODY_COSTS[0]*value*1.5;
                break;
            case BODY_MATERIAL:cost =BODY_COSTS[1]*value*1.5;
                break;
            case BODY_COLOR:cost =BODY_COSTS[2]*value*1.5;
                break;
            case BODY_IP:cost =BODY_COSTS[3]*value*1.5;
                break;
            case BODY_BEZEL:
                cost =BODY_COSTS[4]*value*1.5;
                break;
        }
        return (int) cost;
    }*/

    public static int getOverallCost(Device device){
        double overallCost=0;
        for(Device.DeviceBudget budget:Device.DeviceBudget.values()){
            overallCost+=getCostOfBudget(budget,device);
        }
        //for (int i=0;i<Device.DeviceBudget.values().length;i++){overallCost+=getCostOfBudgets(i,null,-1,device);}
        return (int) Math.round(overallCost);}

    public static int getOverallCost(int[] attributes) {
        Device dev = new Device("a", 0, 0, 0, attributes);
        return getOverallCost(dev);
        /*
        double overallCost=0;
        for (int i=0;i<Device.DeviceBudget.values().length;i++){overallCost+=getCostOfBudgets(i,null,-1,dev);}
        return (int) Math.round(overallCost);
         */
    }
}
