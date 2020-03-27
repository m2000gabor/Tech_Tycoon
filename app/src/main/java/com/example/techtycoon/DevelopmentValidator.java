package com.example.techtycoon;

import static java.lang.Integer.max;

public class DevelopmentValidator {
    private final static int[] SLOT_COSTS={50000,100000,150000,200000,250000,300000,350000,400000,500000,750000,1000000};
    private final static int[][] DEV_COSTS = {
            /*ram*/{100000, 200000, 300000, 500000, 750000, 1000000, 3000000},
            /*memory*/{100000, 200000, 250000, 400000, 600000, 800000, 1000000, 1300000, 1500000, 2500000},
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
    public static int getOneDevelopmentCost(int attrID,int actualLvl){
        if( DEV_COSTS[attrID].length <= actualLvl-1){
            return -1;
        }else{return DEV_COSTS[attrID][actualLvl-1];}
    }

    /*
     * returns -1 if the attribute is maxed out
     * lowest level is 1
     */
    public static int[] getAllDevelopmentCost(int[] levels){
        int[] r=new int[levels.length];
        for(int i=0;i<levels.length;i++){
            r[i]=getOneDevelopmentCost(i,levels[i]);
        }
        return r;
    }

    public static int getMaxLevel(int attrID){
        return DEV_COSTS[attrID].length;
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
        return max(10000+(actualMarketingLevel*30),0);
    }
}
