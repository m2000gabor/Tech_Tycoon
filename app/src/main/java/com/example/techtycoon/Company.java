package com.example.techtycoon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//todo extended company class with statistic
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

    public Company(){};

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
    public boolean incrementLevel(int attributeId){
        if(DevelopmentValidator.getOneDevelopmentCost(attributeId,getLevels_USE_THIS()[attributeId])<= money &&
                DevelopmentValidator.getOneDevelopmentCost(attributeId,getLevels_USE_THIS()[attributeId])!=-1 ){
            this.money-=DevelopmentValidator.getOneDevelopmentCost(attributeId,getLevels_USE_THIS()[attributeId]);
            int[] levels=getLevels_USE_THIS();
            levels[attributeId]++;
            setLevels_USE_THIS(levels);
            return true;
        }else{return false;}
    }
    public boolean hasFreeSlot(){ return maxSlots>usedSlots; }
    public int getMarketValue(){
        int r=0;
        for(int i=0;i<getLevels_USE_THIS().length;i++){
            for (int j = 2; j <= getLevels_USE_THIS()[i]; j++) {
                r+=DevelopmentValidator.getOneDevelopmentCost(i,j);
            }
        }
        for (int i = 2; i <= this.maxSlots; i++) {
            r+=DevelopmentValidator.nextSlotCost(i);
        }
        r*=0.8;
        r+=lastProfit*10;
        return r;
    }
}