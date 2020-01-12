package com.example.techtycoon;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Device {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "pro" +
            "fit")
    public int profit;

    @ColumnInfo(name="ownerCompanyId")
    public int ownerCompanyId;

    public Device(String name,int profit,int ownerCompanyId) {
        this.name = name;
        this.profit = profit;
        this.ownerCompanyId=ownerCompanyId;
    }

    public String getDeviceName(){return this.name;}
    public int  getDeviceId(){return this.id;}
}
