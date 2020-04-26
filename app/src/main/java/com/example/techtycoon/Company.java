package com.example.techtycoon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//todo extended company class with statistic
//todo used slots field should be calculated by a function and not store it
@Entity
public class Company {
    @PrimaryKey(autoGenerate = true)
    public int companyId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "money")
    public int money;

    @ColumnInfo(name = "lastProfit")
    public int lastProfit;

    @ColumnInfo
    public int marketPosition; //The company with the biggest profit is the 1st ...

    //todo use levels as a matrix
    @ColumnInfo(name = "levels")
    private String levels;

    @ColumnInfo
    public int marketing;

    @ColumnInfo
    public int maxSlots;

    @ColumnInfo
    public int usedSlots;

    /*
    @Embedded
    public Assistant assistant;*/

    @ColumnInfo
    public int assistantType;

    @ColumnInfo
    public String assistantStatus;

    @ColumnInfo
    public String logs;

    /*
    @ColumnInfo
    public int reputation;

    @ColumnInfo
    public int popularity;*/

    public Company(){}

    public Company(String name,int money,int[] levels) {
        this.name = name;
        this.money = money;
        this.lastProfit=0;
        this.marketing=0;
        this.maxSlots=1;
        this.usedSlots=0;
        this.levels=Converter.intArrayToString(levels);
        assistantType=-1;
        this.marketPosition=0;
    }

    //getters setters

    public String getCompanyName(){return this.name;}
    public int  getCompanyId(){return this.companyId;}

    public String getLevels() {return levels;}
    public void setLevels(String levels) {this.levels = levels;}
    public void setLevels_USE_THIS(int[] arr){this.levels=Converter.intArrayToString(arr);}
    public int[] getLevels_USE_THIS(){return Converter.stringToIntArray(this.levels);}
    public boolean incrementLevel(Device.DeviceAttribute attribute){
        if(DevelopmentValidator.getOneDevelopmentCost(attribute,getLevelByAttribute(attribute))<= money &&
                DevelopmentValidator.getOneDevelopmentCost(attribute,getLevelByAttribute(attribute))!=-1 ){
            this.money-=DevelopmentValidator.getOneDevelopmentCost(attribute,getLevelByAttribute(attribute));
            int[] levels=getLevels_USE_THIS();
            levels[getLevelIdFromDeviceAttribute(attribute)]++;
            setLevels_USE_THIS(levels);
            return true;
        }else{return false;}
    }
    public boolean hasFreeSlot(){ return maxSlots>usedSlots; }
    public int getMarketValue(){
        int r=0;
        for(Device.DeviceAttribute a : Device.getAllAttribute()){
            for (int j = 2; j <= getLevelByAttribute(a); j++) {
                r+=DevelopmentValidator.getOneDevelopmentCost(a,j);
            }
        }
        for (int i = 2; i <= this.maxSlots; i++) {
            r+=DevelopmentValidator.nextSlotCost(i);
        }
        r*=0.8;
        r+=lastProfit*10;
        return r;
    }

    public boolean producibleByTheCompany(Device d){
        int[] cLevels=this.getLevels_USE_THIS();
        for (int i=0;i<cLevels.length;i++){
            if(cLevels[i]<d.getFieldByAttribute(getDeviceAttributeFromCompanyLevel(i))){return false;}
        }
        return true;
    }

    public static Device.DeviceAttribute getDeviceAttributeFromCompanyLevel(int levelId){
        switch (levelId){
            case 0:return Device.DeviceAttribute.STORAGE_RAM;
            case 1:return Device.DeviceAttribute.STORAGE_MEMORY;
            case 2:return Device.DeviceAttribute.BODY_DESIGN;
            case 3:return Device.DeviceAttribute.BODY_MATERIAL;
            case 4:return Device.DeviceAttribute.BODY_COLOR;
            case 5:return Device.DeviceAttribute.BODY_IP;
            case 6:return Device.DeviceAttribute.BODY_BEZEL;
            default:return null;
        }
    }

    private static int getLevelIdFromDeviceAttribute(Device.DeviceAttribute a){
        switch (a){
            case STORAGE_RAM: return 0;
            case STORAGE_MEMORY: return 1;
            case BODY_DESIGN: return 2;
            case BODY_MATERIAL:return 3;
            case BODY_COLOR:return 4;
            case BODY_IP:return 5;
            case BODY_BEZEL:return 6;
            default:return -1;
        }
    }


    public int getLevelByAttribute(Device.DeviceAttribute attribute){
        switch (attribute){
            case STORAGE_RAM: return getLevels_USE_THIS()[0];
            case STORAGE_MEMORY:return getLevels_USE_THIS()[1];
            case BODY_DESIGN:return getLevels_USE_THIS()[2];
            case BODY_MATERIAL:return getLevels_USE_THIS()[3];
            case BODY_COLOR:return getLevels_USE_THIS()[4];
            case BODY_IP:return getLevels_USE_THIS()[5];
            case BODY_BEZEL:return getLevels_USE_THIS()[6];
            default:return -1;
        }
    }
}