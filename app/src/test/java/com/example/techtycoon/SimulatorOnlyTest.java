package com.example.techtycoon;

import com.example.techtycoon.simulator.Simulator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SimulatorOnlyTest {
    @Test
    //simulator only changes companies' marketing, lastProfit and money
    //simulator doesnt changes devices' attributes and id
    public void simulatorUnchanged(){
        List<Device> deviceList=new ArrayList<>();
        List<Company> companyList=new ArrayList<>();

        //d1
        deviceList.add(Device.getMinimalDevice("d1",10,1));

        //d2
        Device d2=Device.getMinimalDevice("d2",10,1);
        d2.setFieldByAttribute(Device.DeviceAttribute.STORAGE_RAM,2);
        d2.setFieldByAttribute(Device.DeviceAttribute.BODY_COLOR,2);
        d2.cost=DeviceValidator.getOverallCost(d2);
        deviceList.add(d2);

        //d3
        Device d3=Device.getMinimalDevice("d3",20,2);
        for(Device.DeviceAttribute attribute:Device.getAllAttribute()){
            d3.setFieldByAttribute(attribute,2);
        }
        d3.cost=DeviceValidator.getOverallCost(d3);
        deviceList.add(d3);

        Company c1=Company.getMinimalCompany("c1",10);
        c1.companyId=1;
        companyList.add(c1);
        Company c2=Company.getMinimalCompany("c2",1000);
        c2.companyId=2;
        companyList.add(c2);

        Simulator simulator=Simulator.getInstance(deviceList,companyList);
        Wrapped_DeviceAndCompanyList results= simulator.simulate();

        for(int i=0;i<deviceList.size();i++){
            results.devices.get(i).history_SoldPieces=0;
            results.devices.get(i).setSoldPieces(0);
            results.devices.get(i).setTrend(0);
            deviceList.get(i).history_SoldPieces=0;
            deviceList.get(i).setSoldPieces(0);
            deviceList.get(i).setTrend(0);
            assertEquals(deviceList.get(i), results.devices.get(i));
        }
        for(int i=0;i<companyList.size();i++){
            companyList.get(i).money=0;
            companyList.get(i).lastProfit=0;
            results.companies.get(i).money=0;
            results.companies.get(i).lastProfit=0;
            if(companyList.get(i).marketing>=2){
                assertEquals(companyList.get(i).marketing, results.companies.get(i).marketing + 2);
            }else{
                assertEquals( results.companies.get(i).marketing,0);
            }

            companyList.get(i).marketing=0;
            results.companies.get(i).marketing=0;
            assertEquals(companyList.get(i), results.companies.get(i));
        }

    }

    //todo profiles test, include: all attributes are implemented
}
