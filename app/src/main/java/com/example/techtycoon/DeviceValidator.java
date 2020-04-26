package com.example.techtycoon;

//primary source for assistant, should be used in fragments too
public class DeviceValidator {//todo this class structure is really bad
    //TODO real dynamic costs
    private static final double[] MEMORY_COSTS={4,2};
    private static final int[] BODY_COSTS={5,3,1,2,3};

    //todo implement a validation process
    public static boolean validate(Company myCompany,Device newDev){return true;}
    public DeviceValidator(){}

    //calculate the cost of a param or a whole budget
    //@param budgets

    /**
     *
     * @param attribute if null that means all attributes in the budget
     * @param value actual level
     * @return the cost
     */
    private static double getCostOfBudgets(int budget, Device.DeviceAttribute attribute, int value, Device device){
        double cost=0;
        switch (budget){
            //memory
            case 0: if(attribute==null){
                for(Device.DeviceAttribute a : Device.getStorageAttributes()){
                        cost+=getCostOfMemory(a,device.getFieldByAttribute(a));}
                    }else{ cost=getCostOfMemory(attribute,value);}break;
            //body
            case 1: if(attribute==null){
                for(Device.DeviceAttribute a : Device.getBodyAttributes()){
                    cost+=(double) getCostOfBody(a,device.getFieldByAttribute(a)); }
            }else{ cost=(double) getCostOfBody(attribute,value);}break;
        }
        return cost;
    }

    public static double getCostOfMemory(Device.DeviceAttribute a, int value){
        if(a== Device.DeviceAttribute.STORAGE_RAM){
            return value * MEMORY_COSTS[0]*Math.pow(1.15,value);
        }else if(a==Device.DeviceAttribute.STORAGE_MEMORY){
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
    }

    public static int getOverallCost(Device device){
        double overallCost=0;
        for (int i=0;i<Device.NUMBER_OF_BUDGETS;i++){overallCost+=getCostOfBudgets(i,null,-1,device);}
        return (int) Math.round(overallCost);}

    public static int getOverallCost(int[] attributes){
        Device dev=new Device("a",0,0,0,attributes);
        double overallCost=0;
        for (int i=0;i<Device.NUMBER_OF_BUDGETS;i++){overallCost+=getCostOfBudgets(i,null,-1,dev);}
        return (int) Math.round(overallCost);}
}
