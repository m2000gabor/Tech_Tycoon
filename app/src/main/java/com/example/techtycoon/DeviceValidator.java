package com.example.techtycoon;

import static com.example.techtycoon.Simulator.log2;

//primary source for assistant, should be used in fragments too
public class DeviceValidator {
    private static final double[] MEMORY_COSTS={2,4};
    static final int[] BODY_COSTS={5,3,1,2,3};

    //todo implement a validation process
    public static boolean validate(Company myCompany,Device newDev){return true;}
    public DeviceValidator(){}

    //calculate the cost of a param or a whole budget
    //@param budgets
    private static int getCostOfBudgets(int i,int j,int value,Device device){
        double cost=0;
        switch (i){
            //memory
            case 0: if(j==-1){
                        for(int x=0;x<Device.CHILDREN_OF_BUDGETS[i];++x){
                        cost+=getCostOfMemory(x,device.getParams()[i][x]); }
                    }else{ cost=getCostOfMemory(j,value);}break;
            //body
            case 1: if(j==-1){
                for(int x=0;x<Device.CHILDREN_OF_BUDGETS[i];++x){
                    cost+=getCostOfBody(x,device.getParams()[i][x]); }
            }else{ cost=getCostOfBody(j,value);}break;
        }
        return (int) cost;
    }

    static double getCostOfMemory(int j,int value){
        /*
        double cost=0;
        switch(j) {
            case 0:
                cost = (log2(device.memory)+1) * MEMORY_COSTS[j];
                break;
            case 1:
                cost =(log2(device.ram)+1) * MEMORY_COSTS[j];
                break;
        }
        return cost;*/
        return (log2(value)+1) * MEMORY_COSTS[j];
    }

    static double getCostOfBody(int j,int value){
        double cost=0;
        switch(j) {
            //design,material,color,ip,bezel
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                cost =BODY_COSTS[j]*value;
                break;
        }
        return cost;
    }

    public static int getOverallCost(Device device){
        int overallCost=0;
        for (int i=0;i<Device.NUMBER_OF_BUDGETS;i++){overallCost+=getCostOfBudgets(i,-1,-1,device);}
        return overallCost;}
}
