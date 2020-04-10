package com.example.techtycoon;

//primary source for assistant, should be used in fragments too
public class DeviceValidator {
    //TODO real dynamic costs
    private static final double[] MEMORY_COSTS={2,4};
    private static final int[] BODY_COSTS={5,3,1,2,3};

    //todo implement a validation process
    public static boolean validate(Company myCompany,Device newDev){return true;}
    public DeviceValidator(){}

    //calculate the cost of a param or a whole budget
    //@param budgets
    private static double getCostOfBudgets(int i,int j,int value,Device device){
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
                    cost+=(double) getCostOfBody(x,device.getParams()[i][x]); }
            }else{ cost=(double) getCostOfBody(j,value);}break;
        }
        return cost;
    }

    static double getCostOfMemory(int j,int value){
        return value * MEMORY_COSTS[j]*2;
    }

    static int getCostOfBody(int index,int value){
        double cost=0;
        switch(index) {
            //design,material,color,ip,bezel
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                cost =BODY_COSTS[index]*value*1.5;
                break;
        }
        return (int) cost;
    }

    public static int getOverallCost(Device device){
        double overallCost=0;
        for (int i=0;i<Device.NUMBER_OF_BUDGETS;i++){overallCost+=getCostOfBudgets(i,-1,-1,device);}
        return (int) Math.round(overallCost);}

    public static int getOverallCost(int[] attributes){
        Device dev=new Device("a",0,0,0,attributes);
        double overallCost=0;
        for (int i=0;i<Device.NUMBER_OF_BUDGETS;i++){overallCost+=getCostOfBudgets(i,-1,-1,dev);}
        return (int) Math.round(overallCost);}
}
