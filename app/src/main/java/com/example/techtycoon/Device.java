package com.example.techtycoon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//TODO new attributes
//TODO save the name of the owner comapany

@Entity
public class Device {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "profit")
    public int profit;

    @ColumnInfo(name = "cost")
    public int cost;

    @ColumnInfo(name="ownerCompanyId")
    public int ownerCompanyId;

    @ColumnInfo(name="ram")
    public int ram;

    @ColumnInfo(name="memory")
    public int memory;

    @ColumnInfo(name="soldPieces")
    public int soldPieces;

    @ColumnInfo
    public int design;

    @ColumnInfo
    public int material;

    @ColumnInfo
    public int color;

    @ColumnInfo
    public int ip;

    @ColumnInfo
    public int bezel;

    public Device(String name,int profit,int cost,int ownerCompanyId,int ram, int memory) {
        this.name = name;
        this.profit = profit;
        this.cost=cost;
        this.ownerCompanyId=ownerCompanyId;
        this.ram=ram;
        this.memory=memory;
        this.soldPieces=0;
    }

    public void setBodyParams(int design,int materials,int colors,int ip,int bezels){
        this.design=design;
        this.material=materials;
        this.color=colors;
        this.ip=ip;
        this.bezel=bezels;
    }
    public int[] getBodyParams(){
        return new int[]{
        this.design,
        this.material,
        this.color,
        this.ip,
        this.bezel};
    }

    String getDeviceName(){return this.name;}
    //public int  getDeviceId(){return this.id;}
    int getPrice(){return cost+profit;}
}
