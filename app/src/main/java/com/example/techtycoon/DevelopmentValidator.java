package com.example.techtycoon;

import static java.lang.Integer.max;

public class DevelopmentValidator {
    private final static int[] SLOT_COSTS={100000,150000,200000,250000,300000,350000,400000,500000,750000,1000000};
    private final static int[][] DEVELOPMENT_COSTS = {
            /*ram*/{100000, 200000, 300000, 500000, 750000, 1000000, 3000000},
            /*memory*/{100000, 200000, 250000, 400000, 600000, 800000, 1000000, 1300000, 1500000},
            /*design*/{100000, 150000, 200000, 250000, 500000, 750000, 100000, 150000, 2000000},
            /*material*/{20000, 50000, 100000, 150000, 200000, 300000, 500000, 1000000, 1500000},
            /*colors*/{50000, 75000, 100000, 150000, 200000, 250000, 300000},
            /*ip*/{250000, 500000, 1000000, 4000000},
            /*bezels*/{100000, 200000, 250000, 300000, 400000, 600000, 800000, 1000000, 1200000}
    };

    DevelopmentValidator(){}

    /*
    * returns -1 if the attribute is maxed out
    * lowest level is 1
     */
    public static int getOneDevelopmentCost(Device.DeviceAttribute attr, int actualLvl){
        int attrID=-1;
        switch (attr){
            case STORAGE_RAM: attrID =0;break;
            case STORAGE_MEMORY:attrID =1;break;
            case BODY_DESIGN:attrID =2;break;
            case BODY_MATERIAL:attrID =3;break;
            case BODY_COLOR:attrID =4;break;
            case BODY_IP:attrID =5;break;
            case BODY_BEZEL:attrID =6;break;
        }
        if(DEVELOPMENT_COSTS[attrID].length <= actualLvl-1){
            return -1;
        }else{return DEVELOPMENT_COSTS[attrID][actualLvl-1];}
    }

    /*
     * returns -1 if the attribute is maxed out
     * lowest level is 1
     */
    public static int[] getAllDevelopmentCost(int[] levels){
        int[] r=new int[levels.length];
        int i=0;
        for(Device.DeviceAttribute a: Device.getAllAttribute()){
            r[i]=getOneDevelopmentCost(a,levels[i]);
            i++;
        }
        return r;
    }

    public static int getMaxLevel(int attrID){
        return DEVELOPMENT_COSTS[attrID].length;
    }

    /*
     * returns -1 if no more slots available
    */
    public static int nextSlotCost(int actualNumOfSlots){
        if( SLOT_COSTS.length-1 <= actualNumOfSlots-1){
            return -1;
        }else{return SLOT_COSTS[actualNumOfSlots-1];}
    }

    public static int calculateMarketingCost(int actualMarketingLevel){
        return 5000+(actualMarketingLevel*100);
    }
}
