package com.example.techtycoon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(name = "levels")
    private String levels;

    public Company(){};

    public Company(String name,int money,int[] levels) {
        this.name = name;
        this.money = money;
        this.lastProfit=0;
        this.levels=Converter.intArrayToString(levels);
    }

    public String getCompanyName(){return this.name;}
    public int  getCompanyId(){return this.companyId;}

    public String getLevels() {return levels;}
    public void setLevels(String levels) {this.levels = levels;}
    public void setLevels_USE_THIS(int[] arr){this.levels=Converter.intArrayToString(arr);}
    public int[] getLevels_USE_THIS(){return Converter.stringToIntArray(this.levels);}
}