package com.example.techtycoon.Assistant;

import com.example.techtycoon.Company;
import com.example.techtycoon.DetailsOfOneCompany;
import com.example.techtycoon.Device;

import java.util.List;

import androidx.room.ColumnInfo;

public class Assistant {
    @ColumnInfo
    public int assistantType;

    @ColumnInfo
    public int bossCompanyID;

    @ColumnInfo
    public int marketingGoal;

    public Assistant(int bossCompanyID,int marketingGoal){
        this.bossCompanyID =bossCompanyID;
        this.marketingGoal =marketingGoal;
        this.assistantType =1;
    }

    public Company timeToDo(List<Company> companyList, List<Device> deviceList){
        int i=0;
        while (companyList.get(i).companyId != this.bossCompanyID){i++;}
        Company myCompany= companyList.get(i);
        int marketingCost=DetailsOfOneCompany.calculateMarketingCost(myCompany.marketing);
        if (myCompany.money>=marketingCost && myCompany.marketing< marketingGoal){
            myCompany.money-=marketingCost;
            myCompany.marketing+=10;
        }
        return myCompany;
    }
}
